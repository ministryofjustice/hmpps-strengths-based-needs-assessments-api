package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

enum class UserAccess(val value: String) {
  READ_ONLY("READ_ONLY"),
  READ_WRITE("READ_WRITE"),
}
