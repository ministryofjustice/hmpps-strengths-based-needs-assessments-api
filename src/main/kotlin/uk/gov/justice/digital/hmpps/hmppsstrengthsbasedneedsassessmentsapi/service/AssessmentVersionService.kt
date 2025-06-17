package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.util.UUID

@Service
class AssessmentVersionService(
  val assessmentVersionRepository: AssessmentVersionRepository,
  val assessmentVersionAuditRepository: AssessmentVersionAuditRepository,
  val dataMappingService: DataMappingService,
  val telemetryService: TelemetryService,
  val formConfigProvider: FormConfigProvider,
) {
  fun createWith(
    assessment: Assessment,
    answers: Answers = emptyMap(),
  ) = AssessmentVersion(
    assessment = assessment,
    versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
    answers = answers,
  )

  fun clone(assessmentVersion: AssessmentVersion) = with(assessmentVersion) {
    AssessmentVersion(
      assessment = assessment,
      versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
      tag = Tag.UNSIGNED,
      answers = answers,
      oasysEquivalents = oasysEquivalents,
    )
  }

  fun findOrNull(criteria: AssessmentVersionCriteria): AssessmentVersion? {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "updatedAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
  }

  fun find(criteria: AssessmentVersionCriteria): AssessmentVersion {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "updatedAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
      ?: throw AssessmentVersionNotFoundException(criteria)
  }

  fun find(assessmentVersionUuid: UUID): AssessmentVersion = assessmentVersionRepository.findByUuid(assessmentVersionUuid)
    ?: throw AssessmentVersionNotFoundException(assessmentVersionUuid)

  fun findAll(criteria: AssessmentVersionCriteria): List<AssessmentVersion> = assessmentVersionRepository.findAll(criteria.getSpecification(), Sort.by(Sort.Direction.DESC, "updatedAt"))

  fun save(assessmentVersion: AssessmentVersion): AssessmentVersion = assessmentVersionRepository.save(assessmentVersion)

  fun saveAudit(assessmentVersionAudit: AssessmentVersionAudit) = assessmentVersionAuditRepository.save(assessmentVersionAudit)

  fun updateAnswers(existingVersion: AssessmentVersion, request: UpdateAssessmentAnswersRequest) {
    log.info("Adding answers to assessment with UUID ${existingVersion.assessment.uuid}")
    val assessmentVersion = existingVersion.run { takeIf { isUpdatable() } ?: createWith(assessment, answers) }
    val originalStatus = existingVersion.tag
    val originalAnswers = existingVersion.answers

    assessmentVersion
      .apply { setAnswers(request.answersToAdd, request.answersToRemove) }
      .run(::setOasysEquivalents)
      .run(assessmentVersionRepository::save)
      .also { updateAnswersTelemetry(request, it, originalStatus, originalAnswers) }
      .also { log.info("Saved answers to assessment version UUID ${it.uuid}") }
  }

  private fun updateAnswersTelemetry(
    request: UpdateAssessmentAnswersRequest,
    assessmentVersion: AssessmentVersion,
    originalStatus: Tag,
    originalAnswers: Answers,
  ) {
    if (request.answersToAdd.entries.find { (code, answer) ->
        code.endsWith("_user_submitted") && answer.value == "NO"
      } != null
    ) {
      return
    }

    telemetryService.assessmentAnswersUpdated(assessmentVersion, request.userDetails.id, originalStatus)

    fun isAssessmentComplete(answers: Answers) = answers.entries.find { (code, answer) -> code == "assessment_complete" && answer.value == "YES" } != null

    if (isAssessmentComplete(assessmentVersion.answers) && !isAssessmentComplete(originalAnswers)) {
      telemetryService.assessmentCompleted(assessmentVersion, request.userDetails.id)
    }

    val formConfig = assessmentVersion.assessment.info?.run(formConfigProvider::get)
    fun getSectionCode(questionCode: String) = formConfig?.fields[questionCode]?.section ?: "Unknown"

    request.answersToAdd.entries
      .filter { (code, answer) -> code.endsWith("_section_complete") && answer.value == "YES" && originalAnswers[code]?.value != "YES" }
      .map { (fieldCode, _) -> telemetryService.sectionCompleted(assessmentVersion, request.userDetails.id, getSectionCode(fieldCode)) }

    val sectionsUpdated = mutableSetOf<String>()

    listOf(
      request.answersToRemove.filter { originalAnswers.keys.contains(it) }.map { it to true },
      request.answersToAdd
        .filter { originalAnswers[it.key]?.takeIf { existingAnswer -> existingAnswer.equals(it.value) } == null }
        .map { it.key to false },
    )
      .flatten()
      .forEach { (questionCode, isRemoved) ->
        telemetryService.questionUpdated(
          assessmentVersion,
          request.userDetails.id,
          originalStatus,
          getSectionCode(questionCode),
          questionCode,
          isRemoved,
        )

        val sectionCode = getSectionCode(questionCode)
          .takeIf { it != "Unknown" }
        sectionCode?.run(sectionsUpdated::add)
      }

    sectionsUpdated.forEach {
      telemetryService.sectionUpdated(assessmentVersion, request.userDetails.id, originalStatus, it)
    }
  }

  fun setOasysEquivalents(assessmentVersion: AssessmentVersion) = assessmentVersion.apply {
    oasysEquivalents = dataMappingService.getOasysEquivalent(assessmentVersion)
  }

  @Transactional
  fun lock(assessmentVersion: AssessmentVersion, userDetails: UserDetails): AssessmentVersion {
    if (assessmentVersion.tag == Tag.LOCKED_INCOMPLETE) {
      throw ConflictException("The current assessment version is already locked")
    }

    val originalStatus = assessmentVersion.tag
    assessmentVersion.tag = Tag.LOCKED_INCOMPLETE

    telemetryService.assessmentStatusUpdated(assessmentVersion, userDetails.id, originalStatus)

    return assessmentVersionRepository.save(assessmentVersion)
      .audit(userDetails)
      .apply {
        statusFrom = originalStatus
        statusTo = assessmentVersion.tag
      }
      .run(::saveAudit)
      .assessmentVersion
  }

  @Transactional
  fun sign(assessmentVersion: AssessmentVersion, signer: UserDetails, signType: SignType): AssessmentVersion {
    if (assessmentVersion.answers[Field.ASSESSMENT_COMPLETE.lower]?.value != Value.YES.name) {
      throw ConflictException("The current assessment version is not completed.")
    }

    val newStatus: Tag = signType.into()
    val originalStatus = assessmentVersion.tag

    if (assessmentVersion.tag == newStatus) {
      throw ConflictException("The current assessment version is already ${newStatus.name}.")
    }

    assessmentVersion.tag = newStatus

    telemetryService.assessmentStatusUpdated(assessmentVersion, signer.id, originalStatus)

    return assessmentVersionRepository.save(assessmentVersion)
      .audit(signer)
      .apply {
        statusFrom = originalStatus
        statusTo = assessmentVersion.tag
      }
      .run(::saveAudit)
      .assessmentVersion
  }

  @Transactional
  fun counterSign(assessmentVersion: AssessmentVersion, counterSigner: UserDetails, outcome: Tag): AssessmentVersion {
    if (!setOf(Tag.COUNTERSIGNED, Tag.AWAITING_DOUBLE_COUNTERSIGN, Tag.DOUBLE_COUNTERSIGNED, Tag.REJECTED).contains(
        outcome,
      )
    ) {
      throw ConflictException("Invalid outcome status ${outcome.name}.")
    }

    val originalStatus = assessmentVersion.tag
    if (!setOf(Tag.AWAITING_COUNTERSIGN, Tag.AWAITING_DOUBLE_COUNTERSIGN).contains(originalStatus)) {
      throw ConflictException("Cannot counter-sign this assessment version. Unexpected status ${originalStatus.name}.")
    }

    assessmentVersion.tag = outcome

    telemetryService.assessmentStatusUpdated(assessmentVersion, counterSigner.id, originalStatus)

    return assessmentVersionRepository.save(assessmentVersion)
      .audit(counterSigner)
      .apply {
        statusFrom = originalStatus
        statusTo = assessmentVersion.tag
      }
      .run(::saveAudit)
      .assessmentVersion
  }

  @Transactional
  fun rollback(assessmentVersion: AssessmentVersion, userDetails: UserDetails): AssessmentVersion {
    val originalStatus = assessmentVersion.tag

    if (!Tag.tagsThatCanRollback().contains(assessmentVersion.tag)) {
      throw ConflictException("Cannot rollback this assessment version. Unexpected status ${originalStatus.name}.")
    }

    assessmentVersion.tag = Tag.ROLLED_BACK

    telemetryService.assessmentStatusUpdated(assessmentVersion, userDetails.id, originalStatus)

    return assessmentVersionRepository.save(assessmentVersion)
      .audit(userDetails)
      .apply {
        statusFrom = originalStatus
        statusTo = assessmentVersion.tag
      }
      .run(::saveAudit)
      .assessmentVersion
  }

  @Transactional
  fun softDelete(assessmentVersions: List<AssessmentVersion>, userDetails: UserDetails): List<AssessmentVersion> = assessmentVersions
    .filter { !it.deleted }
    .ifEmpty { throw ConflictException("No assessment versions found for deletion") }
    .map { it.apply { deleted = true } }
    .run(assessmentVersionRepository::saveAll)
    .also { telemetryService.assessmentSoftDeleted(it.first().assessment, userDetails.id, it) }

  @Transactional
  fun undelete(assessment: Assessment, fromVersion: Int, toVersion: Int?, userDetails: UserDetails): List<AssessmentVersion> = assessmentVersionRepository.findAllDeleted(assessment.uuid)
    .filter {
      if (toVersion == null) {
        it.versionNumber >= fromVersion
      } else {
        it.versionNumber >= fromVersion && it.versionNumber < toVersion
      }
    }
    .ifEmpty { throw ConflictException("No assessment versions found for un-deletion") }
    .map { it.apply { deleted = false } }
    .run(assessmentVersionRepository::saveAll)
    .also { telemetryService.assessmentUndeleted(assessment, userDetails.id, it) }

  fun findAllByAssessment(assessment: Assessment): List<AssessmentVersion> = assessmentVersionRepository.findAllByAssessmentUuid(assessment.uuid)

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
