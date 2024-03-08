package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.UserAccess

class CreateSessionRequest(
  @Schema(required = true, description = "EOR Session", example = "ABC12345678")
  val userSessionId: String,

  @Schema(required = true, description = "User display name", example = "Probation User")
  val userDisplayName: String,

  @Schema(required = true, description = "User access", example = "READ_ONLY")
  val userAccess: UserAccess,

  @Schema(required = true, description = "OASys assessment ID", example = "ABC12345678")
  val oasysAssessmentPk: String,
)
