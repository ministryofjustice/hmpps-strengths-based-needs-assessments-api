package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

class AuditedRequest(
  @Schema(description = "OASys User")
  @Valid
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
