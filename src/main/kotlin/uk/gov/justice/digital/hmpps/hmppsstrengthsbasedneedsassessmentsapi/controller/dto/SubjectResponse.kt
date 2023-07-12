package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

data class SubjectResponse(
  val givenName: String,
  val familyName: String,
  val dateOfBirth: String,
  val crn: String,
  val pnc: String,
)
