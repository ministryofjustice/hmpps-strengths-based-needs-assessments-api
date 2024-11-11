package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

class AuditedRequest(
  @Schema(description = "User")
  @get:Valid
  override val userDetails: UserDetails,
) : AuditableRequest
