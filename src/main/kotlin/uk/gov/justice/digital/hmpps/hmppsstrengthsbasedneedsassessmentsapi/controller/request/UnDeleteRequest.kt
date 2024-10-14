package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.PositiveOrZero

class UnDeleteRequest(
  @Schema(description = "The Assessment version number to un-delete from (inclusive).", example = "1")
  @PositiveOrZero
  val versionFrom: Int,
  @Schema(description = "The Assessment version number to un-delete to (exclusive).", example = "2")
  @PositiveOrZero
  val versionTo: Int? = null,
  @Schema(description = "User")
  @Valid
  override val userDetails: UserDetails,
) : AuditableRequest
