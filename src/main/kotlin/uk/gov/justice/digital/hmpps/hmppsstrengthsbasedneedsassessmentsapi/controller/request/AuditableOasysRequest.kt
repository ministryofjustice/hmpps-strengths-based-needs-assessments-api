package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints

class OasysUserDetails(
  @Schema(description = "User ID", example = "111111")
  @Size(max = Constraints.OASYS_USER_ID_MAX_LENGTH)
  val id: String = "",
  @Schema(description = "User name", example = "John Doe")
  @Size(max = Constraints.OASYS_USER_NAME_MAX_LENGTH)
  val name: String = "",
)

interface AuditableOasysRequest {
  val userDetails: OasysUserDetails
}
