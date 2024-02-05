package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class AssociateAssessmentRequest(
  @Schema(required = true, description = "OASys Assessment PK to create an association for", example = "ABC12345678")
  val oasysAssessmentPk: String,
  @Schema(description = "Optionally provide an old OASys Assessment PK. The new PK will be associated to the same SAN assessment", example = "ABC12345678")
  val oldOasysAssessmentPk: String? = null,
)
