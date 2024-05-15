package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentSubject
import java.time.LocalDate

data class SubjectResponse(
  val givenName: String,
  val familyName: String,
  val dateOfBirth: LocalDate?,
  val crn: String?,
  val pnc: String?,
) {
  companion object {
    fun from(subject: AssessmentSubject): SubjectResponse {
      with(subject.subjectDetails) {
        return SubjectResponse(
          givenName,
          familyName,
          dateOfBirth,
          crn,
          pnc,
        )
      }
    }
  }
}
