package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.CreateSession
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.SessionService
import java.util.UUID

@RestController
class SessionController(
  val sessionService: SessionService,
) {
  @RequestMapping(path = ["/session/create"], method = [RequestMethod.POST])
  fun getOneTimeLink(
    @RequestBody request: CreateSession,
  ): OneTimeLinkResponse {
    return sessionService.createOneTimeLink(request)
  }

  @RequestMapping(path = ["/session/{uuid}"], method = [RequestMethod.GET])
  fun useOneTimeLink(
    @PathVariable uuid: UUID,
  ): SessionResponse? {
    return sessionService.useOneTimeLink(uuid)
  }
}
