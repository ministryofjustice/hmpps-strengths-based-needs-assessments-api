package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SubjectResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@Service
class SubjectService(
  val assessmentRepository: AssessmentRepository,
) {
  fun getSubject(assessmentUuid: UUID): SubjectResponse {
    log.info("Returning subject for assessment with UUID $assessmentUuid")
    return assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        SubjectResponse(
          "Paul",
          "Whitfield",
          "01/01/1970",
          "A123456",
          "01/123456789A",
        )
      }
      ?: throw AssessmentNotFoundException("Not assessment found with ID $assessmentUuid")
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
