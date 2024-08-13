package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AuditedRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CounterSignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.MergeAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.Message
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.RollbackAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.OasysAssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import io.swagger.v3.oas.annotations.tags.Tag as SwaggerTag

@RestController
@SwaggerTag(name = "OASys Assessment")
@RequestMapping("/oasys/assessment")
class OasysAssessmentController(
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentService: OasysAssessmentService,
) {
  @RequestMapping(path = ["/{oasysAssessmentPK}"], method = [RequestMethod.GET])
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
    "hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_OASYS', 'ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')",
  )
  fun get(
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
  ): OasysAssessmentVersionResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid) }
      .run(assessmentVersionService::find)
      .run(OasysAssessmentVersionResponse::from)
  }

  @RequestMapping(path = ["/create"], method = [RequestMethod.POST])
  @Operation(description = "Associate an OASys assessment PK with a SAN assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment associated successfully"),
      ApiResponse(
        responseCode = "404",
        description = "Previous assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "An association already exists for the provided OASys Assessment PK",
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
  fun create(
    @RequestBody @Valid
    request: CreateAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.associateExistingOrCreate(
      request.oasysAssessmentPk,
      request.previousOasysAssessmentPk,
      request.regionPrisonCode,
    )
      .run { AssessmentVersionCriteria(uuid) }
      .run(assessmentVersionService::find)
      .run {
        when (tag) {
          Tag.UNSIGNED -> this
          else -> assessmentVersionService.createWith(assessment, answers, oasysEquivalents).run(assessmentVersionService::save)
        }
      }
      .run(OasysAssessmentResponse::from)
  }

  @RequestMapping(path = ["/merge"], method = [RequestMethod.POST])
  @Operation(description = "Transfer associated SAN assessment from one PK to another")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessments associated successfully"),
      ApiResponse(
        responseCode = "404",
        description = "Previous assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "An association already exists for the provided OASys Assessment PK",
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
  fun merge(
    @RequestBody @Valid
    request: MergeAssessmentRequest,
  ): Message {
    oasysAssessmentService.transferAssociation(request.merge)
    return Message("Successfully processed all ${request.merge.size} merge elements")
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/sign"], method = [RequestMethod.POST])
  @Operation(description = "Signs the latest version of an assessment identified by the provided OASys Assessment PK")
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody
    @Valid
    request: SignAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid) }
      .run(assessmentVersionService::find)
      .let {
        assessmentVersionService.sign(it, UserDetails.from(request), request.signType)
      }
      .run(OasysAssessmentResponse::from)
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/counter-sign"], method = [RequestMethod.POST])
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody @Valid
    request: CounterSignAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid, versionNumber = request.sanVersionNumber) }
      .run(assessmentVersionService::find)
      .let {
        assessmentVersionService.counterSign(it, UserDetails.from(request), request.outcome)
      }
      .run(OasysAssessmentResponse::from)
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/lock"], method = [RequestMethod.POST])
  @Operation(description = "Locks the latest version of an assessment identified by the provided OASys Assessment PK")
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody @Valid
    request: AuditedRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid) }
      .run(assessmentVersionService::find)
      .let { assessmentVersionService.lock(it, UserDetails.from(request)) }
      .run(OasysAssessmentResponse::from)
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/rollback"], method = [RequestMethod.POST])
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody @Valid
    request: RollbackAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid, versionNumber = request.sanVersionNumber) }
      .run(assessmentVersionService::find)
      .let { assessmentVersionService.rollback(it, UserDetails.from(request)) }
      .run(OasysAssessmentResponse::from)
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/soft-delete"], method = [RequestMethod.POST])
  @Operation(description = "Soft-deletes an OASys assessment.")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OASys assessment has been soft-deleted"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "Unable to soft-delete an assessment that has already been soft-deleted",
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody @Valid
    request: AuditedRequest,
  ): Message {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .run(oasysAssessmentService::softDelete)
      .run { Message("Successfully soft-deleted OASys assessment PK $oasysAssessmentPK") }
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/undelete"], method = [RequestMethod.POST])
  @Operation(description = "Undeletes an OASys assessment.")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OASys assessment has been undeleted"),
      ApiResponse(
        responseCode = "404",
        description = "Assessment not found",
        content = arrayOf(Content(schema = Schema(implementation = ErrorResponse::class))),
      ),
      ApiResponse(
        responseCode = "409",
        description = "Unable to undelete an assessment that is not deleted",
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
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    @Size(min = Constraints.OASYS_PK_MIN_LENGTH, max = Constraints.OASYS_PK_MAX_LENGTH)
    @Valid
    oasysAssessmentPK: String,
    @RequestBody @Valid
    request: AuditedRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.undelete(oasysAssessmentPK)
      .run { AssessmentVersionCriteria(assessment.uuid) }
      .run(assessmentVersionService::find)
      .run(OasysAssessmentResponse::from)
  }
}
