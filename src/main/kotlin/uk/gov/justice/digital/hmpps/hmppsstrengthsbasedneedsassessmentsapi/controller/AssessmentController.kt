package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.OasysAssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OasysAssessmentAlreadyExistsException
import java.util.UUID

@RestController
@Tag(name = "Assessment Controller")
class AssessmentController(
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentService: OasysAssessmentService,
) {
  @RequestMapping(path = ["/assessment/create"], method = [RequestMethod.POST])
  @Operation(description = "Create an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment created"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun createAssessment(
    @RequestBody
    request: CreateAssessmentRequest,
  ) {
    oasysAssessmentService.createAssessmentWithOasysId(request.oasysAssessmentPk)
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}/version/{tag}/answers"], method = [RequestMethod.GET])
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
    @Parameter(description = "Tag", required = true, example = "validated")
    @PathVariable
    tag: String,
  ): Answers {
    return assessmentVersionService.find(AssessmentVersionCriteria(assessmentUuid, tag)).answers
  }

  @RequestMapping(path = ["/assessment/{assessmentUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get the latest version of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
      ApiResponse(responseCode = "404", description = "No assessment was found for the specified criteria", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun getAssessment(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @Parameter(description = "Timestamp after which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    after: Long? = null,
    @Parameter(description = "Timestamp until which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    until: Long? = null,
    @Parameter(description = "Assessment version tag to filter by", `in` = ParameterIn.QUERY, example = "1706879012")
    tag: String? = null,
    @Parameter(description = "Assessment status to filter by", `in` = ParameterIn.QUERY, example = "COMPLETE")
    status: String? = null,
  ): AssessmentResponse {
    val assessmentVersion = assessmentVersionService.find(AssessmentVersionCriteria(assessmentUuid, tag, after, until, status))
    return AssessmentResponse(assessmentVersion)
  }

  @RequestMapping(path = ["/assessment-oasys/{oasysAssessmentPK}"], method = [RequestMethod.GET])
  @Operation(description = "Get the latest version of an assessment by OASys Assessment PK")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
      ApiResponse(responseCode = "404", description = "No assessment was found for the specified criteria", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun getAssessments(
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    oasysAssessmentPK: String,
    @Parameter(description = "Timestamp after which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    after: Long? = null,
    @Parameter(description = "Timestamp until which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    until: Long? = null,
    @Parameter(description = "Assessment version tag to filter by", `in` = ParameterIn.QUERY, example = "1706879012")
    tag: String? = null,
    @Parameter(description = "Assessment status to filter by", `in` = ParameterIn.QUERY, example = "COMPLETE")
    status: String? = null,
  ): AssessmentResponse {
    val oasysAssessment = oasysAssessmentService.find(oasysAssessmentPK)
    val assessmentVersion = assessmentVersionService.find(AssessmentVersionCriteria(oasysAssessment.assessment.uuid, tag, after, until, status))
    return AssessmentResponse(assessmentVersion)
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
    request: UpdateAssessmentAnswersRequest,
  ) {
    assessmentVersionService.updateAnswers(assessmentUuid, request)
  }

  @RequestMapping(path = ["/assessment/associate"], method = [RequestMethod.POST])
  @Operation(description = "Associate an OASys assessment with a SAN assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment associated successfully"),
      ApiResponse(responseCode = "404", description = "Assessment not found", content = arrayOf(Content())),
      ApiResponse(responseCode = "409", description = "An association already exists for the provided OASys Assessment PK", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun associateAssessment(
    @RequestBody
    request: AssociateAssessmentRequest,
  ): ResponseEntity<AssessmentResponse> {
    try {
      return ResponseEntity.ok(AssessmentResponse(oasysAssessmentService.associate(request)))
    } catch (e: OasysAssessmentAlreadyExistsException) {
      throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
    }
  }
}
