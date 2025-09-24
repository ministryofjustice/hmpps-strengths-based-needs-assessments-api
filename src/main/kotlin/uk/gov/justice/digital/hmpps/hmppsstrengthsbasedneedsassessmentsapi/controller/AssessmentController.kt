package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AuditedRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CounterSignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.RollbackAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.SignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.SoftDeleteRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UnDeleteRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserLocation
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.TelemetryService
import java.util.UUID
import io.swagger.v3.oas.annotations.tags.Tag as SwaggerTag

@RestController
@SwaggerTag(name = "Assessment")
@RequestMapping("/assessment")
class AssessmentController(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val telemetryService: TelemetryService,
) {
  @RequestMapping(path = ["/{assessmentUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get the latest version of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment found"),
      ApiResponse(
        responseCode = "404",
        description = "No assessment was found for the specified criteria",
        content = arrayOf(Content()),
      ),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun get(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @Parameter(
      description = "Version number",
      `in` = ParameterIn.QUERY,
      example = "1",
    )
    versionNumber: Int? = null,
  ): AssessmentResponse = AssessmentVersionCriteria(
    assessmentUuid = assessmentUuid,
    versionNumber = versionNumber,
  )
    .run(assessmentVersionService::find)
    .run(AssessmentResponse::from)

  @RequestMapping(path = ["/version/{assessmentVersionUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get the requested version of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment Version found"),
      ApiResponse(
        responseCode = "404",
        description = "The assessment version was not found",
        content = arrayOf(Content()),
      ),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun getVersion(
    @Parameter(
      description = "Assessment Version UUID",
      required = true,
      example = "123e4567-e89b-12d3-a456-426614174000",
    )
    @PathVariable
    assessmentVersionUuid: UUID,
  ): AssessmentResponse = assessmentVersionService.find(assessmentVersionUuid)
    .run(AssessmentResponse::from)

  @RequestMapping(method = [RequestMethod.POST])
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Create an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "201", description = "Assessment created"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun create(
    @RequestBody @Valid
    request: AuditedRequest,
  ): AssessmentResponse = if (request.userDetails.location != null) {
    request.userDetails
  } else {
    request.userDetails.copy(location = UserLocation.COMMUNITY)
  }.let { userDetails ->
    assessmentService.create(userDetails.location!!)
      .assessmentVersions.first()
      .audit(userDetails)
      .run(assessmentVersionService::saveAudit)
      .also { telemetryService.assessmentCreated(it.assessmentVersion, userDetails.id) }
      .run { AssessmentResponse.from(assessmentVersion) }
  }

  @RequestMapping(path = ["/{assessmentUuid}/clone"], method = [RequestMethod.POST])
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(description = "Clone the latest version of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "201", description = "Assessment version created"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun clone(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: AuditedRequest,
  ): AssessmentResponse {
    val original = AssessmentVersionCriteria(assessmentUuid).run(assessmentVersionService::find)
    return assessmentVersionService.clone(original)
      .run(assessmentVersionService::save)
      .apply {
        audit(request.userDetails)
          .run(assessmentVersionService::saveAudit)
      }
      .also { telemetryService.assessmentCreated(it, request.userDetails.id, original.versionNumber) }
      .run(AssessmentResponse::from)
  }

  @RequestMapping(path = ["/{assessmentUuid}/answers"], method = [RequestMethod.POST])
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
    @RequestBody @Valid
    request: UpdateAssessmentAnswersRequest,
  ) {
    AssessmentVersionCriteria(assessmentUuid)
      .run(assessmentVersionService::find)
      .also { assessmentVersionService.updateAnswers(it, request) }
  }

  @RequestMapping(path = ["/{assessmentUuid}/sign"], method = [RequestMethod.POST])
  @Operation(description = "Signs the latest version of an assessment identified by the provided Assessment UUID")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment version signed successfully"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "The assessment could not be signed. See details in error message.",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun sign(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: SignAssessmentRequest,
  ): AssessmentResponse = AssessmentVersionCriteria(assessmentUuid).run(assessmentVersionService::find)
    .let { assessmentVersionService.sign(it, request.userDetails, request.signType) }
    .run(AssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}/counter-sign"], method = [RequestMethod.POST])
  @Operation(description = "Marks an assessment version as counter-signed.")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment version counter-signed successfully"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "The assessment could not be counter-signed. See details in error message.",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun counterSign(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: CounterSignAssessmentRequest,
  ): AssessmentResponse {
    val originalVersion = AssessmentVersionCriteria(assessmentUuid, versionNumber = request.versionNumber)
      .run(assessmentVersionService::find)
    return originalVersion
      .let { assessmentVersionService.counterSign(it, request.userDetails, request.outcome) }
      .run(AssessmentResponse::from)
  }

  @RequestMapping(path = ["/{assessmentUuid}/lock"], method = [RequestMethod.POST])
  @Operation(description = "Locks the latest version of an assessment identified by the provided Assessment UUID")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment version locked successfully"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "The latest version of the assessment has already been locked",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun lock(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: AuditedRequest,
  ): AssessmentResponse = AssessmentVersionCriteria(assessmentUuid).run(assessmentVersionService::find)
    .let { assessmentVersionService.lock(it, request.userDetails) }
    .run(AssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}/rollback"], method = [RequestMethod.POST])
  @Operation(description = "Create a new \"ROLLBACK\" version of an existing assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "ROLLBACK version created"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "Unable to create ROLLBACK for latest assessment version",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun rollback(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: RollbackAssessmentRequest,
  ): AssessmentResponse = AssessmentVersionCriteria(assessmentUuid, versionNumber = request.versionNumber)
    .run(assessmentVersionService::find)
    .let { assessmentVersionService.rollback(it, request.userDetails) }
    .run(AssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}/soft-delete"], method = [RequestMethod.POST])
  @Operation(description = "Soft-deletes a range of assessment versions.")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment versions have been soft-deleted"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "Unable to soft-delete requested assessment versions",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun softDelete(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: SoftDeleteRequest,
  ): AssessmentResponse? = request.toAssessmentVersionCriteria(assessmentUuid)
    .also { assessmentService.findByUuid(assessmentUuid) }
    .run(assessmentVersionService::findAll)
    .let { assessmentVersionService.softDelete(it, request.userDetails) }
    .let { AssessmentVersionCriteria(it.first().assessment.uuid) }
    .run(assessmentVersionService::findOrNull)
    ?.run(AssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}/undelete"], method = [RequestMethod.POST])
  @Operation(description = "Undeletes an OASys assessment.")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment versions have been un-deleted"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "Unable to un-delete the requested assessment versions",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun undelete(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
    @RequestBody @Valid
    request: UnDeleteRequest,
  ): AssessmentResponse = assessmentService.findByUuid(assessmentUuid)
    .let { with(request) { assessmentVersionService.undelete(it, versionFrom, versionTo, userDetails) } }
    .let { AssessmentVersionCriteria(it.first().assessment.uuid) }
    .run(assessmentVersionService::find)
    .run(AssessmentResponse::from)

  @RequestMapping(path = ["/{assessmentUuid}/all"], method = [RequestMethod.GET])
  @Operation(description = "Gets all assessment versions")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment versions found"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "500",
        description = "Unexpected error",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun findAllByAssessmentUuid(
    @Parameter(description = "Assessment UUID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    assessmentUuid: UUID,
  ): List<AssessmentVersionResponse> = assessmentService.findByUuid(assessmentUuid)
    .run(assessmentVersionService::findAllByAssessment)
    .run(AssessmentVersionResponse::fromAll)
    .sortedByDescending { it.createdAt }
}
