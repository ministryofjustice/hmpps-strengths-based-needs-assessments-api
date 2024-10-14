package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException

@Service
class AssessmentVersionService(
  val assessmentVersionRepository: AssessmentVersionRepository,
  val assessmentVersionAuditRepository: AssessmentVersionAuditRepository,
  val dataMappingService: DataMappingService,
) {
  fun getPreviousOrCreate(assessment: Assessment): AssessmentVersion {
    return findOrNull(AssessmentVersionCriteria(assessment.uuid))?.let { assessmentVersion ->
      if (assessmentVersion.isUpdatable()) assessmentVersion else createWith(assessment, assessmentVersion.answers)
    } ?: createWith(assessment)
  }

  fun createWith(
    assessment: Assessment,
    answers: Answers = emptyMap(),
    oasysEquivalents: OasysEquivalent = emptyMap(),
  ) = AssessmentVersion(
    assessment = assessment,
    versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
    answers = answers,
    oasysEquivalents = oasysEquivalents,
  )

  fun findOrNull(criteria: AssessmentVersionCriteria): AssessmentVersion? {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "updatedAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
  }

  fun find(criteria: AssessmentVersionCriteria): AssessmentVersion {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "updatedAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
      ?: throw AssessmentVersionNotFoundException(criteria)
  }

  fun findAll(criteria: AssessmentVersionCriteria): List<AssessmentVersion> =
    assessmentVersionRepository.findAll(criteria.getSpecification(), Sort.by(Sort.Direction.DESC, "updatedAt"))

  fun save(assessmentVersion: AssessmentVersion): AssessmentVersion {
    return assessmentVersionRepository.save(assessmentVersion)
  }

  fun updateAnswers(assessment: Assessment, request: UpdateAssessmentAnswersRequest) {
    log.info("Adding answers to assessment with UUID ${assessment.uuid}")

    getPreviousOrCreate(assessment)
      .apply { setAnswers(request.answersToAdd, request.answersToRemove) }
      .run(::setOasysEquivalents)
      .run(assessmentVersionRepository::save)
      .also { log.info("Saved answers to assessment version UUID ${it.uuid}") }
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

    return assessmentVersionRepository.save(assessmentVersion).also {
      AssessmentVersionAudit(
        assessmentVersion = it,
        userDetails = userDetails,
        statusFrom = originalStatus,
        statusTo = it.tag,
      ).run(assessmentVersionAuditRepository::save)
    }
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

  @Transactional
  fun softDelete(assessmentVersions: List<AssessmentVersion>, userDetails: UserDetails): List<AssessmentVersion> {
    return assessmentVersions
      .filter { !it.deleted }
      .ifEmpty { throw ConflictException("No assessment versions found for deletion") }
      .map { it.apply { deleted = true } }
      .run(assessmentVersionRepository::saveAll)
      .also {
        val versionNumbers = it.map { version -> version.versionNumber }.joinToString(", ")
        log.info("Successfully soft-deleted assessment versions $versionNumbers. User ID ${userDetails.id}")
      }
  }

  @Transactional
  fun undelete(assessment: Assessment, fromVersion: Int, toVersion: Int?, userDetails: UserDetails): List<AssessmentVersion> {
    return assessmentVersionRepository.findAllDeleted(assessment.uuid)
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
      .also {
        val versionNumbers = it.map { version -> version.versionNumber }.joinToString(", ")
        log.info("Successfully un-deleted assessment versions $versionNumbers. User ID ${userDetails.id}")
      }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
