package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.CreateSession
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.UseOneTimeLinkRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentFormInfoRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.SessionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OneTimeLinkException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.UserNotAuthenticatedException
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
class SessionService(
  val sessionRepository: SessionRepository,
  val assessmentService: AssessmentService,
  val applicationConfig: ApplicationConfig,
  val assessmentFormInfoRepository: AssessmentFormInfoRepository,
) {
  fun createOneTimeLink(request: CreateSession): OneTimeLinkResponse {
    val assessment = assessmentService.findOrCreateAssessment(request.oasysAssessmentId, request.crn)

    return sessionRepository.save(
      Session(
        userSessionId = request.userSessionId,
        userDisplayName = request.userDisplayName,
        userAccess = request.userAccess,
        assessment = assessment,
      ),
    ).let {
      log.info("Session created for OASys assessment ID: ${request.oasysAssessmentId}")
      assessment.info?.let { assessmentInfo ->
        val (majorVersion, minorVersion) = assessmentInfo.formVersion.split(".")
        OneTimeLinkResponse("${applicationConfig.baseUrl}/sbna-poc/$majorVersion/$minorVersion/start?sessionId=${it.linkUuid}")
      } ?: OneTimeLinkResponse("${applicationConfig.baseUrl}/sbna-poc/start?sessionId=${it.linkUuid}")
    }
  }

  private fun sessionHasExpired(session: Session): Boolean {
    return Duration.between(session.createdAt, LocalDateTime.now()).toHours() > applicationConfig.sessionMaxAge
  }

  fun useOneTimeLink(uuid: UUID, request: UseOneTimeLinkRequest): SessionResponse? {
    val session = sessionRepository.findByLinkUuidAndLinkStatus(uuid, LinkStatus.UNUSED)
      ?: throw OneTimeLinkException("One time link has been used")

    if (sessionHasExpired(session)) throw OneTimeLinkException("One time link has expired")

    session.linkStatus = LinkStatus.USED

    if (session.assessment.info == null) {
      assessmentFormInfoRepository.save(
        AssessmentFormInfo(formName = request.form, formVersion = request.version, assessment = session.assessment),
      )
    }

    return sessionRepository.save(session).let {
      log.info("Used one time link: ${it.linkUuid}")
      SessionResponse.from(it)
    }
  }

  fun checkSessionIsValid(uuid: UUID) {
    sessionRepository.findSessionByUuid(uuid)
      ?.let { checkOasysSessionStatus(it.userSessionId) }
      ?: throw UserNotAuthenticatedException("User session does not exist")
  }

  fun checkOasysSessionStatus(oasysSessionId: String) {}

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
