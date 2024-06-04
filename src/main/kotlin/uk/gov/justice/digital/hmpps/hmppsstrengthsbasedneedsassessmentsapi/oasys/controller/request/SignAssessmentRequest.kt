package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType

class SignAssessmentRequest(
  @Schema(description = "Indicates what type of case this is")
  val signType: SignType,
  @Schema(description = "OASys User ID", example = "111111")
  val oasysUserID: String,
  @Schema(description = "OASys User name", example = "John Doe")
  val oasysUserName: String,
)
