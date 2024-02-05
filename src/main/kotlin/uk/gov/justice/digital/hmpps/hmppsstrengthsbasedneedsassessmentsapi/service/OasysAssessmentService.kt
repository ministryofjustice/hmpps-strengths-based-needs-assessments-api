package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OasysAssessmentNotFoundException

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String): OasysAssessment {
    val assessment = assessmentService.createAssessment()
    assessmentVersionService.create("unvalidated", assessment)
    return oasysAssessmentRepository.save(OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = assessment))
      .also { log.info("Created assessment for OASys assessment PK: ${it.oasysAssessmentPk}") }
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

    var oasysAssessment: OasysAssessment

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

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
