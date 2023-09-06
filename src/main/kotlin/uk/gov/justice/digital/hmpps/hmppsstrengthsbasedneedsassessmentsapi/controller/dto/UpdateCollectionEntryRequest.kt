package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.UpdateAssessmentAnswersDto

data class UpdateCollectionEntryRequest(
  val index: Int,
  val answers: UpdateAssessmentAnswersDto,
)
