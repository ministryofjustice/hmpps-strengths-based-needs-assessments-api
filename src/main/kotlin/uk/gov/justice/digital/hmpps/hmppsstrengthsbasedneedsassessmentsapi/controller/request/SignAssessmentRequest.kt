package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType

class SignAssessmentRequest(
  @Schema(description = "Indicates what type of case this is")
  val signType: SignType,
  @Schema(description = "User")
  @get:Valid
  override val userDetails: UserDetails,
) : AuditableRequest
