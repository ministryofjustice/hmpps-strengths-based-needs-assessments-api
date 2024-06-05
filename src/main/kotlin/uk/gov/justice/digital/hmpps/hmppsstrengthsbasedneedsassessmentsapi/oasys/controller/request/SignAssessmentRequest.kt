package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType

class SignAssessmentRequest(
  @Schema(description = "Indicates what type of case this is")
  val signType: SignType,
  @Schema(description = "OASys User")
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
