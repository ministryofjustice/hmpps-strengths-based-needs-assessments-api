package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentId: String): Assessment {
    return assessmentRepository.save(Assessment(oasysAssessmentId = oasysAssessmentId))
      .also { log.info("Created assessment for OASys assessment ID: ${it.oasysAssessmentId}") }
  }

  fun findOrCreateAssessment(oasysAssessmentId: String): Assessment {
    return assessmentRepository.findByOasysAssessmentId(oasysAssessmentId)
      ?: createAssessmentWithOasysId(oasysAssessmentId)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
