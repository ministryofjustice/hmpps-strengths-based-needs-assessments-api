package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.UpdateCollectionEntryRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import java.util.UUID

@RestController
@Tag(name = "Assessment Controller")
class AssessmentController(
  val assessmentService: AssessmentService,
) {
  @RequestMapping(path = ["/assessment/{assessmentUuid}/answers"], method = [RequestMethod.GET])
  @Operation(description = "Get answers for an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
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
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun updateAnswers(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody
    answers: UpdateAssessmentAnswersDto,
  ) {
    assessmentService.updateAnswers(assessmentUuid, answers)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/collection/{collectionName}"], method = [RequestMethod.POST])
  @Operation(description = "Add answers to an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Added entry to collection"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun addEntryToCollection(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @PathVariable
    collectionName: String,
    @RequestBody
    answers: UpdateAssessmentAnswersDto,
  ) {
    assessmentService.addEntryToCollection(assessmentUuid, collectionName, answers)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/collection/{collectionName}"], method = [RequestMethod.PUT])
  @Operation(description = "Update answers in collection")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Updated entry in collection"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun updateEntryInCollection(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @PathVariable
    collectionName: String,
    @RequestBody
    answers: UpdateCollectionEntryRequest,
  ) {
    assessmentService.updateEntryInCollection(assessmentUuid, collectionName, answers)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/collection/{collectionName}/index/{indexToReturn}"], method = [RequestMethod.GET])
  @Operation(description = "Get entry from collection")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Returned entry from collection"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun getEntryFromCollection(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @PathVariable
    collectionName: String,
    @PathVariable
    indexToReturn: Int,
  ): Answers {
    return assessmentService.getEntryFromCollection(assessmentUuid, collectionName, indexToReturn)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/collection/{collectionName}/index/{indexToRemove}"], method = [RequestMethod.DELETE])
  @Operation(description = "Remove entry from collection")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Removed entry from collection"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun removeEntryFromCollection(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @PathVariable
    collectionName: String,
    @PathVariable
    indexToRemove: Int,
  ) {
    assessmentService.removeEntryFromCollection(assessmentUuid, collectionName, indexToRemove)
  }
}
