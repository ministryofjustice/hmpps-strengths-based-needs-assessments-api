package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class LockAssessmentRequest(
  @Schema(description = "OASys User")
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
