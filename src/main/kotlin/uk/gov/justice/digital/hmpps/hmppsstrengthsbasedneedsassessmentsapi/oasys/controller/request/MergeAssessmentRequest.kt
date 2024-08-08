package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid

class MergeAssessmentRequest(
  @Schema(description = "OASys assessment PKs to merge")
  @Valid
  val merge: List<TransferAssociationRequest>,
  @Schema(description = "OASys User")
  @Valid
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
