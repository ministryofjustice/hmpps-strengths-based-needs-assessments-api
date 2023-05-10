package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
) {
  fun findOrCreateAssessment(oasysAssessmentId: String): Assessment {
    return assessmentRepository.findByOasysAssessmentId(oasysAssessmentId)
      ?: assessmentRepository.save(Assessment(oasysAssessmentId = oasysAssessmentId))
  }
}
