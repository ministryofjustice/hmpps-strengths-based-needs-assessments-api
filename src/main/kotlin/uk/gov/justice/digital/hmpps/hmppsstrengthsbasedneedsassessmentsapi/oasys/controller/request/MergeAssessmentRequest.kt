package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class MergeAssessmentRequest(
  @Schema(description = "OASys assessment PKs to merge")
  val merge: List<TransferAssociationRequest>,
  @Schema(description = "OASys User")
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
