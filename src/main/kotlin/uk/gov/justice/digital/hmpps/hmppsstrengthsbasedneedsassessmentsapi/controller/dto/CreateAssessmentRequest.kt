package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

class CreateAssessmentRequest(
  @Schema(required = true, description = "OASys assessment ID", example = "ABC12345678")
  val oasysAssessmentPk: String,
)
