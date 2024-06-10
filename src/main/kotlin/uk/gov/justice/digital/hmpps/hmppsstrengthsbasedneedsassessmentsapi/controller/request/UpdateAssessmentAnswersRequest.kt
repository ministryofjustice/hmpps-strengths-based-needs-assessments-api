package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class UpdateAssessmentAnswersRequest(
  val answersToAdd: Answers = emptyMap(),
  val answersToRemove: List<String> = emptyList(),
)
