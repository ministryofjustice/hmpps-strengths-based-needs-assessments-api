package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.PositiveOrZero
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag

class CounterSignAssessmentRequest(
  @Schema(description = "The SAN Assessment version number that was returned from the Sign Assessment API call.", example = "2")
  @PositiveOrZero
  val sanVersionNumber: Int,
  @Schema(description = "Indicates what type of case this is")
  val outcome: Tag,
  @Schema(description = "OASys User")
  @Valid
  override val userDetails: OasysUserDetails,
) : AuditableOasysRequest
