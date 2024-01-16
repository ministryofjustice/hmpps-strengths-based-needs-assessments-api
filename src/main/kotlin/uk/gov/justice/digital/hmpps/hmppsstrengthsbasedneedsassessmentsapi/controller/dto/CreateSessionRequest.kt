package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

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
