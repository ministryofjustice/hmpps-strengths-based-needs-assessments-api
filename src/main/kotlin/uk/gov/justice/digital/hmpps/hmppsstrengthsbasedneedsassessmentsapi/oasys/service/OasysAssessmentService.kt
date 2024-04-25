package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyLockedException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String): OasysAssessment {
    val assessment = Assessment()
    assessment.assessmentVersions = listOf(
      AssessmentVersion(assessment = assessment, versionNumber = 0, tag = Tag.UNVALIDATED),
      AssessmentVersion(assessment = assessment, versionNumber = 1, tag = Tag.VALIDATED),
    )
    assessment.oasysAssessments = listOf(OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = assessment))
    val persistedAssessment = assessmentService.save(assessment).also { log.info("Assessment created for OASys PK $oasysAssessmentPk") }

    return persistedAssessment.oasysAssessments.first()
  }

  fun findOrCreateAssessment(oasysAssessmentPk: String): OasysAssessment {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) ?: createAssessmentWithOasysId(oasysAssessmentPk)
  }

  fun find(oasysAssessmentPk: String): OasysAssessment? {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
  }

  fun associate(oasysAssessmentPk: String, previousOasysAssessmentPk: String? = null): Assessment {
    find(oasysAssessmentPk)?.let { throw OasysAssessmentAlreadyExistsException(oasysAssessmentPk) }

    val assessment = previousOasysAssessmentPk?.let {
      val oldOasysAssessment = find(previousOasysAssessmentPk)
        ?: throw OasysAssessmentNotFoundException(previousOasysAssessmentPk)
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = oldOasysAssessment.assessment,
      )
      oasysAssessmentRepository.save(oasysAssessment)
      oasysAssessment.assessment
    } ?: run {
      val oasysAssessment = createAssessmentWithOasysId(oasysAssessmentPk)
      oasysAssessment.assessment
    }

    log.info("Associated OASys assessment PK $oasysAssessmentPk with SAN assessment ${assessment.uuid}")
    return assessment
  }

  fun checkAlreadyLocked(assessmentVersion: AssessmentVersion, oasysAssessmentPk: String) {
    if (assessmentVersion.tag == Tag.LOCKED_INCOMPLETE) {
      throw OasysAssessmentAlreadyLockedException(oasysAssessmentPk)
    }
  }

  fun lock(oasysAssessmentPk: String): AssessmentVersion {
    val oasysAssessment = find(oasysAssessmentPk) ?: throw OasysAssessmentAlreadyExistsException(oasysAssessmentPk)
    val criteria = AssessmentVersionCriteria(oasysAssessment.assessment.uuid, Tag.validatedTags())

    return assessmentVersionService.find(criteria)?.let {
      checkAlreadyLocked(it, oasysAssessmentPk)
      assessmentVersionService.cloneAndTag(it, Tag.LOCKED_INCOMPLETE)
    } ?: throw AssessmentVersionNotFoundException(criteria)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
