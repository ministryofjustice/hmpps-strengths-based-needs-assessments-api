package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentRequest
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

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String): OasysAssessment {
    val assessment = Assessment()
    assessment.assessmentVersions = listOf(AssessmentVersion(assessment = assessment))
    assessment.oasysAssessments = listOf(OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = assessment))
    val persistedAssessment = assessmentService.save(assessment).also { log.info("Assessment created for OASys PK $oasysAssessmentPk") }

    return persistedAssessment.oasysAssessments.first()
  }

  fun findOrCreateAssessment(oasysAssessmentPk: String): OasysAssessment {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) ?: createAssessmentWithOasysId(oasysAssessmentPk)
  }

  fun find(oasysAssessmentPk: String): OasysAssessment {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
      ?: throw OasysAssessmentNotFoundException("No OASys assessment found for PK $oasysAssessmentPk")
  }

  fun associate(request: AssociateAssessmentRequest): AssessmentVersion {
    try {
      find(request.oasysAssessmentPk)
      throw OasysAssessmentAlreadyExistsException(request.oasysAssessmentPk)
    } catch (_: OasysAssessmentNotFoundException) {
    }

    val oasysAssessment: OasysAssessment

    if (request.oldOasysAssessmentPk == null) {
      oasysAssessment = createAssessmentWithOasysId(request.oasysAssessmentPk)
    } else {
      val oldOasysAssessment = find(request.oldOasysAssessmentPk)
      oasysAssessment = OasysAssessment(oasysAssessmentPk = request.oasysAssessmentPk, assessment = oldOasysAssessment.assessment)
      oasysAssessmentRepository.save(oasysAssessment).also {
        log.info("Associated OASys assessment PK ${it.oasysAssessmentPk}")
      }
    }

    return assessmentVersionService.find(AssessmentVersionCriteria(oasysAssessment.assessment.uuid))
  }

  fun lock(oasysAssessmentPk: String): AssessmentVersion {
    val oasysAssessment = find(oasysAssessmentPk)
    val assessmentVersion = assessmentVersionService.find(AssessmentVersionCriteria(oasysAssessment.assessment.uuid))
    if (assessmentVersion.tag == Tag.LOCKED) {
      throw OasysAssessmentAlreadyLockedException(oasysAssessmentPk)
    }
    return assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
