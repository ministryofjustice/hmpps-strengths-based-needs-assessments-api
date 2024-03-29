package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.OasysAssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
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
      ApiResponse(responseCode = "404", description = "No assessment was found for the specified criteria", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun getAssessment(
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    oasysAssessmentPK: String,
    @Parameter(description = "Timestamp after which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    after: Long? = null,
    @Parameter(description = "Timestamp until which the latest assessment should be returned", `in` = ParameterIn.QUERY, example = "1706879012")
    until: Long? = null,
    @Parameter(description = "Assessment version tag to filter by", `in` = ParameterIn.QUERY, example = "1706879012")
    tag: Tag? = null,
    @Parameter(description = "Assessment status to filter by", `in` = ParameterIn.QUERY, example = "COMPLETE")
    status: String? = null,
  ): AssessmentResponse {
    val oasysAssessment = oasysAssessmentService.find(oasysAssessmentPK)
    val assessmentVersion = assessmentVersionService.find(AssessmentVersionCriteria(oasysAssessment.assessment.uuid, tag, after, until, status))
    return AssessmentResponse(assessmentVersion)
  }

  @RequestMapping(path = ["/associate"], method = [RequestMethod.POST])
  @Operation(description = "Associate OASys assessments with a SAN assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment(s) associated successfully"),
      ApiResponse(responseCode = "404", description = "Assessment(s) not found", content = arrayOf(Content())),
      ApiResponse(responseCode = "409", description = "An association already exists for the provided OASys Assessment PK", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun associateAssessment(
    @RequestBody
    request: AssociateAssessmentsRequest,
  ): ResponseEntity<String> {
    for (pair in request.associate) {
      oasysAssessmentService.associate(pair)
    }
    return ResponseEntity.ok("OK")
  }

  @RequestMapping(path = ["/{oasysAssessmentPK}/lock"], method = [RequestMethod.POST])
  @Operation(description = "Locks the latest version of an assessment identified by the provided OASys Assessment PK")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Assessment version locked successfully"),
      ApiResponse(responseCode = "404", description = "Assessment not found", content = arrayOf(Content())),
      ApiResponse(responseCode = "409", description = "The latest version of the assessment has already been locked", content = arrayOf(Content())),
      ApiResponse(responseCode = "500", description = "Unexpected error", content = arrayOf(Content())),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun lockAssessment(
    @Parameter(description = "OASys Assessment PK", required = true, example = "oasys-pk-goes-here")
    @PathVariable
    oasysAssessmentPK: String,
  ): AssessmentResponse {
    return AssessmentResponse(oasysAssessmentService.lock(oasysAssessmentPK))
  }
}
