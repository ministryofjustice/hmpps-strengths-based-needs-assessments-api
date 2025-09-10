package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.flywaydb.core.api.Location
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints

interface AuditableRequest {
  val userDetails: UserDetails
}

data class UserDetails(
  @Schema(description = "User ID", example = "111111")
  @get:Size(max = Constraints.OASYS_USER_ID_MAX_LENGTH)
  val id: String = "",
  @Schema(description = "User name", example = "John Doe")
  @get:Size(max = Constraints.OASYS_USER_NAME_MAX_LENGTH)
  val name: String = "",
  @Schema(description = "User type", example = "SAN")
  val type: UserType = UserType.SAN,
  @Schema(description = "Location", example = "COMMUNITY")
  val location: StaffLocation? = null,
)

enum class StaffLocation {
  PRISON,
  COMMUNITY,
}

enum class UserType {
  OASYS,
  SAN,
}
