package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class UpdateAssessmentAnswersRequest(
  val tags: List<String>,
  val answersToAdd: Answers = emptyMap(),
  val answersToRemove: List<String> = emptyList(),
)
