package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class RollbackAssessmentRequest(
  @Schema(description = "The SAN Assessment version number that was returned from the Sign Assessment API call.", example = "2")
  val sanVersionNumber: Long,
  @Schema(description = "OASys User")
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
