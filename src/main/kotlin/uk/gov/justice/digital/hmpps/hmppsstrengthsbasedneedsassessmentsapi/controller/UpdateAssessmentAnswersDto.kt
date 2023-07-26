package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

@RestController
@Tag(name = "Assessment Controller")
class UpdateAssessmentAnswersDto(
  val answersToAdd: Answers = emptyMap(),
  val answersToRemove: List<String> = emptyList(),
)
