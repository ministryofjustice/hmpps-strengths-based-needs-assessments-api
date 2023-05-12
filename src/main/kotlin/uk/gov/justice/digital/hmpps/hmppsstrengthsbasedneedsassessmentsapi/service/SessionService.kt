package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.CreateSession
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.SessionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OneTimeLinkException
import java.util.UUID

@Service
class SessionService(
  val sessionRepository: SessionRepository,
  val assessmentService: AssessmentService,
) {
  fun createOneTimeLink(request: CreateSession): OneTimeLinkResponse {
    val assessment = assessmentService.findOrCreateAssessment(request.oasysAssessmentId)

    return sessionRepository.save(
      Session(
        userSessionId = request.userSessionId,
        userAccess = request.userAccess,
        assessment = assessment,
      ),
    ).let {
      log.info("Session created for OASys assessment ID: ${request.oasysAssessmentId}")
      OneTimeLinkResponse.from(it)
    }
  }

  fun useOneTimeLink(uuid: UUID): SessionResponse? {
    val session = sessionRepository.findByLinkUuidAndLinkStatus(uuid, LinkStatus.UNUSED)
      ?: throw OneTimeLinkException("One time link has been used")

    if (session.hasExpired()) throw OneTimeLinkException("One time link has expired")

    session.linkStatus = LinkStatus.USED

    return sessionRepository.save(session).let {
      log.info("Used one time link: ${it.linkUuid}")
      SessionResponse.from(it)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
