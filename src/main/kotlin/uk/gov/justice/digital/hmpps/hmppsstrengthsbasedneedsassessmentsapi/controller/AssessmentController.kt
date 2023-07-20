package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import java.util.UUID

@RestController
@Tag(name = "Assessment Controller")
class AssessmentController(
  val assessmentService: AssessmentService,
) {
  @RequestMapping(path = ["/assessment/assessmentUuid/answers"], method = [RequestMethod.GET])
  @Operation(description = "Get answers for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
    ],
  )
  fun getAnswers(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
  ): Answers {
    return assessmentService.getAnswers(assessmentUuid)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/answers"], method = [RequestMethod.POST])
  @Operation(description = "Add answers to an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment updated"),
    ],
  )
  fun addAnswersToAssessment(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody
    answers: Answers,
  ) {
    assessmentService.addAnswers(assessmentUuid, answers)
  }
}
