package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateOneTimeLinkRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateSessionRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.UseOneTimeLinkRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.UserInformation
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.UserSessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.SessionService
import java.util.UUID

@RestController
@Tag(name = "OASys Session")
@RequestMapping("/oasys/session")
class SessionController(
  val sessionService: SessionService,
) {

  // For future us: Migrate UI to call new endpoint and remove this one
  @RequestMapping(path = ["/create"], method = [RequestMethod.POST])
  @Operation(description = "Initialise a session for a given OASys assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Session created"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_CREATE_SESSION')")
  fun createOneTimeLink(
    @RequestBody
    request: CreateSessionRequest,
  ): OneTimeLinkResponse {
    return sessionService.createOneTimeLink(
      CreateOneTimeLinkRequest(
        oasysAssessmentPk = request.oasysAssessmentPk,
        user = UserInformation(
          identifier = request.userSessionId,
          displayName = request.userDisplayName,
          accessMode = request.userAccess,
        ),
      ),
    )
  }

  // For future us: Migrate UI to call new endpoint and remove this one
  @RequestMapping(path = ["/{uuid}"], method = [RequestMethod.POST])
  @Operation(description = "Use a one time link for a given session")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "One time link valid and has been marked as used"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_READ')")
  fun useOneTimeLink(
    @Parameter(description = "One time link ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    uuid: UUID,
    @RequestBody
    request: UseOneTimeLinkRequest,
  ): SessionResponse? {
    return sessionService.useOneTimeLink(uuid, request)?.let {
      SessionResponse(
        it.uuid,
        it.user.identifier,
        it.user.displayName,
        it.user.accessMode,
        it.assessmentUUID,
        it.oasysAssessmentPk,
      )
    }
  }

  @RequestMapping(path = ["/one-time-link"], method = [RequestMethod.POST])
  @Operation(description = "Initialise a session for a given OASys assessment")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Session created, one time link issued"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_CREATE_SESSION')")
  fun getOneTimeLink(
    @RequestBody
    request: CreateOneTimeLinkRequest,
  ): OneTimeLinkResponse {
    return sessionService.createOneTimeLink(request)
  }

  @RequestMapping(path = ["/{uuid}/one-time-link"], method = [RequestMethod.POST])
  @Operation(description = "Use a one time link for a given session")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "One time link valid and has been marked as used"),
    ],
  )
  @PreAuthorize("hasRole('ROLE_STRENGTHS_AND_NEEDS_READ')")
  fun getSessionWithOneTimeLink(
    @Parameter(description = "One time link ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    uuid: UUID,
    @RequestBody
    request: UseOneTimeLinkRequest,
  ): UserSessionResponse? {
    return sessionService.useOneTimeLink(uuid, request)
  }

  @RequestMapping(path = ["/{uuid}/validate"], method = [RequestMethod.GET])
  @Operation(description = "Check status of OASys session")
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "OASys session is active"),
      ApiResponse(responseCode = "401", description = "OASys session has expired"),
    ],
  )
  @PreAuthorize("hasAnyRole('ROLE_STRENGTHS_AND_NEEDS_READ', 'ROLE_STRENGTHS_AND_NEEDS_WRITE')")
  fun checkOASysSessionStatus(
    @Parameter(description = "Session ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    @PathVariable
    uuid: UUID,
  ) {
    return sessionService.checkSessionIsValid(uuid)
  }
}
