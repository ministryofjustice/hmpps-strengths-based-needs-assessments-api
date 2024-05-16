package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SubjectDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentSubjectService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.SubjectNotFoundException
import java.util.UUID

@RestController
@Tag(name = "Subject")
@RequestMapping("/subject")
class SubjectController(
  private val assessmentSubjectService: AssessmentSubjectService,
) {
  @RequestMapping(path = ["/{assessmentUuid}"], method = [RequestMethod.GET])
  @Operation(description = "Get the subject of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "One time link used"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_READ')")
  fun getSubject(
    @Parameter(description = "OASys assessment ID", required = true, example = "ABC2134567890")
    @PathVariable
    assessmentUuid: UUID,
  ): SubjectDetails {
    return assessmentSubjectService.findByAssessmentUuid(assessmentUuid)?.subjectDetails
      ?: throw SubjectNotFoundException("Subject not found for assessment UUID: $assessmentUuid")
  }
}
