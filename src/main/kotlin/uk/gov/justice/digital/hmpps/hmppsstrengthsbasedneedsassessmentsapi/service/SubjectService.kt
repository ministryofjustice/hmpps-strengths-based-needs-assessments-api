package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SubjectResponse

@Service
class SubjectService() {
  fun getSubject(assessmentId: String): SubjectResponse {
    log.info("Returning subject for assessment: $assessmentId")
    return SubjectResponse(
      "Paul",
      "Whitfield",
      "01/01/1970",
      "A123456",
      "01/123456789A",
    )
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
