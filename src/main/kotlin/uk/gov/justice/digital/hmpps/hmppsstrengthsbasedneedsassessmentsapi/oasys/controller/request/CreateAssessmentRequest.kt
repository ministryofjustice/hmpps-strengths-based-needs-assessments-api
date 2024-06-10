package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema

class CreateAssessmentRequest(
  @Schema(required = true, description = "OASys Assessment PK to create an association for", example = "222222")
  val oasysAssessmentPk: String,
  @Schema(
    description = "Optionally provide an old OASys Assessment PK. " +
      "The new PK will be associated to the same SAN assessment",
    example = "111111",
  )
  val previousOasysAssessmentPk: String? = null,
  @Schema(description = "Region prison code", example = "111111")
  val regionPrisonCode: String? = null,
  @Schema(description = "OASys User")
  val userDetails: OasysUserDetails? = null,
)
