package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CounterSignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.Message
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SignAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.TransferAssociationRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.OasysAssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserType
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
    oasysAssessmentPK: String,
  ): OasysAssessmentVersionResponse {
    val oasysAssessment = oasysAssessmentService.find(oasysAssessmentPK)

    val criteria = AssessmentVersionCriteria(oasysAssessment.assessment.uuid, Tag.validatedTags())
    val assessmentVersion = assessmentVersionService.find(criteria)

    return OasysAssessmentVersionResponse.from(assessmentVersion)
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
    @RequestBody
    request: CreateAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.associateExistingOrCreate(request.oasysAssessmentPk, request.previousOasysAssessmentPk, request.regionPrisonCode)
      .let { assessmentVersionService.find(AssessmentVersionCriteria(it.uuid, Tag.validatedTags())) }
      .let { OasysAssessmentResponse.from(it.assessment.uuid, it.versionNumber) }
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
    @RequestBody
    request: List<TransferAssociationRequest>,
  ): Message {
    oasysAssessmentService.transferAssociation(request)
    return Message("Successfully processed all ${request.size} merge elements")
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
    oasysAssessmentPK: String,
    @RequestBody
    request: SignAssessmentRequest,
  ): OasysAssessmentResponse {
    val signedVersion = oasysAssessmentService.sign(oasysAssessmentPK, request.counterSignType)
    return OasysAssessmentResponse.from(signedVersion.assessment.uuid, signedVersion.versionNumber)
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
    oasysAssessmentPK: String,
    @RequestBody
    request: CounterSignAssessmentRequest,
  ): OasysAssessmentResponse {
    return oasysAssessmentService.find(oasysAssessmentPK)
      .let {
        val criteria = AssessmentVersionCriteria(it.assessment.uuid, versionNumber = request.sanVersionNumber)
        assessmentVersionService.find(criteria)
      }
      .let {
        val counterSigner = UserDetails(request.counterSignerID, request.counterSignerName, UserType.OASYS)
        assessmentVersionService.counterSign(it, counterSigner, request.outcome)
      }
      .let {
        OasysAssessmentResponse.from(it.assessment.uuid, it.versionNumber)
      }
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
    oasysAssessmentPK: String,
  ): OasysAssessmentResponse {
    val lockedVersion = oasysAssessmentService.lock(oasysAssessmentPK)
    return OasysAssessmentResponse.from(lockedVersion.assessment.uuid, lockedVersion.versionNumber)
  }
}
