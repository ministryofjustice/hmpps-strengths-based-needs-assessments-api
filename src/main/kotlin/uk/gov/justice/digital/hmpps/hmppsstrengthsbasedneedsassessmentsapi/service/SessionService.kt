package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.CreateSession
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.SessionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OneTimeLinkException
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
class SessionService(
  val sessionRepository: SessionRepository,
  val assessmentService: AssessmentService,
  val applicationConfig: ApplicationConfig,
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
      OneTimeLinkResponse("${applicationConfig.baseUrl}/${it.linkUuid}")
    }
  }

  private fun sessionHasExpired(session: Session): Boolean {
    return Duration.between(session.createdAt, LocalDateTime.now()).toHours() > applicationConfig.sessionMaxAge
  }

  fun useOneTimeLink(uuid: UUID): SessionResponse? {
    val session = sessionRepository.findByLinkUuidAndLinkStatus(uuid, LinkStatus.UNUSED)
      ?: throw OneTimeLinkException("One time link has been used")

    if (sessionHasExpired(session)) throw OneTimeLinkException("One time link has expired")

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
