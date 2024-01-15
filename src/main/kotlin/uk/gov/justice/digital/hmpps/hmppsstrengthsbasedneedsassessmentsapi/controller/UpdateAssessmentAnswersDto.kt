package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class UpdateAssessmentAnswersDto(
  val tags: List<String>,
  val answersToAdd: Answers = emptyMap(),
  val answersToRemove: List<String> = emptyList(),
)
