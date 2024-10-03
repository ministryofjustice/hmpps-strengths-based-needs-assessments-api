package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.CreateAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import java.util.*

@RestController
@Tag(name = "Coordinator")
@RequestMapping("/coordinator/assessment")
class CoordinatorController(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
) {

  @RequestMapping(method = [RequestMethod.POST])
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Create an assessment and return id")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "201", description = "Assessment created"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun createAssessment(
    @RequestBody
    createAssessmentRequest: CreateAssessmentRequest,
  ) = assessmentService.createAndAudit(createAssessmentRequest.userDetails).run(CreateAssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get the latest version of an assessment by OASys Assessment PK")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
      ApiResponse(
        responseCode = "404",
        description = "No assessment was found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize(
    "hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ')",
  )
  fun get(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
  ): OasysAssessmentVersionResponse {
    return AssessmentVersionCriteria(assessmentUuid)
      .run(assessmentVersionService::find)
      .run(OasysAssessmentVersionResponse::from)
  }
}
