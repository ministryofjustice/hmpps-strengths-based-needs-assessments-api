package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SubjectResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.SubjectService

@RestController
@Tag(name = "Session Controller")
class SubjectController(
  val subjectService: SubjectService,
) {
  @RequestMapping(path = ["/subject/{assessmentId}"], method = [RequestMethod.GET])
  @Operation(description = "Get the subject of an assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "One time link used"),
    ],
  )
  fun useOneTimeLink(
    @Parameter(description = "OASys assessment ID", required = true, example = "ABC2134567890")
    @PathVariable
    assessmentId: String,
  ): SubjectResponse {
    return subjectService.getSubject(assessmentId)
  }
}
