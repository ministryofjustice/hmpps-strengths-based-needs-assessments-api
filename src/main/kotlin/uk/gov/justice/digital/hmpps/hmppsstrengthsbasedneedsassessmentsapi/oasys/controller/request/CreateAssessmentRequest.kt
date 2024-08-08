package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints

class CreateAssessmentRequest(
  @Schema(required = true, description = "OASys Assessment PK to create an association for", example = "222222")
  @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
  val oasysAssessmentPk: String,
  @Schema(
    description = "Optionally provide an old OASys Assessment PK. " +
      "The new PK will be associated to the same SAN assessment",
    example = "111111",
  )
  @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
  val previousOasysAssessmentPk: String? = null,
  @Schema(description = "Region prison code", example = "111111")
  @Size(max = Constraints.REGION_PRISON_CODE_MAX_LENGTH)
  val regionPrisonCode: String? = null,
  @Schema(description = "OASys User")
  @Valid
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
