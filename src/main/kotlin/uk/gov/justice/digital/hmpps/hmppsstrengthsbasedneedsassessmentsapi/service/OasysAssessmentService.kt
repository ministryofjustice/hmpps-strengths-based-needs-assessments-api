package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.OasysAssessmentRepository

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String): OasysAssessment {
    val assessment = assessmentService.createAssessment()
    assessmentVersionService.findOrCreate("unvalidated", assessment)
    return oasysAssessmentRepository.save(OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = assessment))
      .also { log.info("Created assessment for OASys assessment PK: ${it.oasysAssessmentPk}") }
  }

  fun findOrCreateAssessment(oasysAssessmentPk: String): OasysAssessment {
    val oasysAssessment = oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
      ?: createAssessmentWithOasysId(oasysAssessmentPk)
    assessmentVersionService.cloneFromPrevious("validated", oasysAssessment.assessment)
    return oasysAssessment
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
