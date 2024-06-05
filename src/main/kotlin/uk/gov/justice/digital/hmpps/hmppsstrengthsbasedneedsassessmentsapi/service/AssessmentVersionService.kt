package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AssessmentVersionService(
  val assessmentVersionRepository: AssessmentVersionRepository,
  val assessmentVersionAuditRepository: AssessmentVersionAuditRepository,
  val dataMappingService: DataMappingService,
) {
  private fun versionUpdatedToday(assessmentVersion: AssessmentVersion): Boolean {
    val versionUpdatedDate = assessmentVersion.updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    return versionUpdatedDate == today
  }

  fun getPreviousOrCreate(tag: Tag, assessment: Assessment): AssessmentVersion? {
    if (!Tag.validatedTags().contains(tag)) {
      return findOrNull(AssessmentVersionCriteria(assessment.uuid, setOf(tag)))
    }

    return findOrNull(AssessmentVersionCriteria(assessment.uuid, Tag.validatedTags()))?.let {
      getPrevious(it, tag, assessment)
    } ?: createNew(tag, assessment)
  }

  private fun createNew(
    tag: Tag,
    assessment: Assessment,
  ) = AssessmentVersion(
    tag = tag,
    assessment = assessment,
    versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
  )

  private fun getPrevious(
    assessmentVersion: AssessmentVersion,
    tag: Tag,
    assessment: Assessment,
  ) = if (versionUpdatedToday(assessmentVersion) && !Tag.lockedTags().contains(assessmentVersion.tag)) {
    assessmentVersion
  } else {
    AssessmentVersion(
      tag = tag,
      assessment = assessment,
      answers = assessmentVersion.answers,
      versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
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

  fun save(assessmentVersion: AssessmentVersion): AssessmentVersion {
    return assessmentVersionRepository.save(assessmentVersion)
  }

  fun updateAnswers(assessment: Assessment, request: UpdateAssessmentAnswersRequest) {
    if (request.tags.intersect(Tag.lockedTags()).isNotEmpty()) {
      throw ConflictException("Locked versions cannot be updated")
    }

    log.info("Adding answers to assessment with UUID ${assessment.uuid} for tags ${request.tags}")

    request.tags.map { tag ->
      getPreviousOrCreate(tag, assessment)?.run {
        answers = answers.plus(request.answersToAdd)
          .filterNot { thisAnswer -> request.answersToRemove.contains(thisAnswer.key) }
        updatedAt = LocalDateTime.now()
        setOasysEquivalent(this)

        assessmentVersionRepository.save(this)

        log.info("Saved answers to assessment version UUID $uuid")
      } ?: throw AssessmentVersionNotFoundException(AssessmentVersionCriteria(assessment.uuid, setOf(tag)))
    }
  }

  fun setOasysEquivalent(assessmentVersion: AssessmentVersion): AssessmentVersion {
    return assessmentVersion.apply {
      oasys_equivalent = dataMappingService.getOasysEquivalent(assessmentVersion)
    }
  }

  @Transactional
  fun lock(assessmentVersion: AssessmentVersion): AssessmentVersion {
    if (assessmentVersion.tag == Tag.LOCKED_INCOMPLETE) {
      throw ConflictException("The current assessment version is already locked")
    }

    val originalStatus = assessmentVersion.tag
    assessmentVersion.tag = Tag.LOCKED_INCOMPLETE

    return assessmentVersionRepository.save(assessmentVersion).also {
      AssessmentVersionAudit(
        assessmentVersion = it,
        statusFrom = originalStatus,
        statusTo = it.tag,
      ).run(assessmentVersionAuditRepository::save)
    }
  }

  @Transactional
  fun sign(assessmentVersion: AssessmentVersion, signType: SignType, signer: UserDetails): AssessmentVersion {
    if (assessmentVersion.answers[Field.ASSESSMENT_COMPLETE.lower]?.value != Value.YES.name) {
      throw ConflictException("The current assessment version is not completed.")
    }

    val newStatus: Tag = signType.into()
    val originalStatus = assessmentVersion.tag

    if (assessmentVersion.tag == newStatus) {
      throw ConflictException("The current assessment version is already ${newStatus.name}.")
    }

    assessmentVersion.tag = newStatus

    return assessmentVersionRepository.save(assessmentVersion).also {
      AssessmentVersionAudit(
        assessmentVersion = it,
        userDetails = signer,
        statusFrom = originalStatus,
        statusTo = it.tag,
      ).run(assessmentVersionAuditRepository::save)
    }
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

    return assessmentVersionRepository.save(assessmentVersion)
      .also {
        AssessmentVersionAudit(
          assessmentVersion = it,
          userDetails = counterSigner,
          statusFrom = originalStatus,
          statusTo = it.tag,
        ).run(assessmentVersionAuditRepository::save)
      }
  }

  @Transactional
  fun rollback(assessmentVersion: AssessmentVersion, userDetails: UserDetails): AssessmentVersion {
    val originalStatus = assessmentVersion.tag

    if (!Tag.tagsThatCanRollback().contains(assessmentVersion.tag)) {
      throw ConflictException("Cannot rollback this assessment version. Unexpected status ${originalStatus.name}.")
    }

    assessmentVersion.tag = Tag.ROLLED_BACK

    return assessmentVersionRepository.save(assessmentVersion)
      .also {
        AssessmentVersionAudit(
          assessmentVersion = it,
          userDetails = userDetails,
          statusFrom = originalStatus,
          statusTo = it.tag,
        ).run(assessmentVersionAuditRepository::save)
      }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
