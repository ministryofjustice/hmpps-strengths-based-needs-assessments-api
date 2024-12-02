package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class UpdateAssessmentAnswersRequest(
  val answersToAdd: Answers = emptyMap(),
  val answersToRemove: List<String> = emptyList(),
  @Schema(description = "User")
  @get:Valid
  override val userDetails: UserDetails,
) : AuditableRequest
