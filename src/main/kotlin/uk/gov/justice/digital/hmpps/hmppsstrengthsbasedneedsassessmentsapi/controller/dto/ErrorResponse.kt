package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

data class ErrorResponse(
  val userMessage: String = "Something went wrong",
  val developerMessage: String = "An exception occurred, check the logs",
  val moreInfo: String? = null,
)
