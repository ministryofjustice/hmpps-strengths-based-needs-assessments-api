package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.serialize.DebugStringSerializer

data class ErrorResponse(
  val userMessage: String = "Something went wrong",
  @JsonSerialize(using = DebugStringSerializer::class)
  val developerMessage: String = "An exception occurred, check the logs",
  val moreInfo: String? = null,
)
