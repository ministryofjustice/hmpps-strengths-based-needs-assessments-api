package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

enum class CounterSignType {
  SELF,
  COUNTERSIGN,
}

class SignAssessmentRequest(
  @Schema(description = "Indicates what type of case this is")
  val counterSignType: CounterSignType,
  @Schema(description = "OASys User ID", example = "111111")
  val oasysUserID: String,
  @Schema(description = "OASys User name", example = "John Doe")
  val oasysUserName: String,
)
