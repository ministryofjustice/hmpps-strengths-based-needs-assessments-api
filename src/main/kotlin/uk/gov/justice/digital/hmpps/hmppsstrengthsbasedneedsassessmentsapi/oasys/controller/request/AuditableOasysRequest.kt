package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class OasysUserDetails(
  @Schema(description = "User ID", example = "111111")
  val id: String = "",
  @Schema(description = "User name", example = "John Doe")
  val name: String = "",
)

interface AuditableOasysRequest {
  val userDetails: OasysUserDetails
}
