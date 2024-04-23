package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateOneTimeLinkRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.UseOneTimeLinkRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.SessionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OneTimeLinkException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentFormInfoRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentSubjectService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.UserNotAuthenticatedException
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

@Service
class SessionService(
  val sessionRepository: SessionRepository,
  val oasysAssessmentService: OasysAssessmentService,
  val applicationConfig: ApplicationConfig,
  val assessmentFormInfoRepository: AssessmentFormInfoRepository,
  val assessmentSubjectService: AssessmentSubjectService,
) {
  fun createOneTimeLink(request: CreateOneTimeLinkRequest): OneTimeLinkResponse {
    val oasysAssessment = oasysAssessmentService.findOrCreateAssessment(request.oasysAssessmentPk)

    request.subjectDetails?.let {
      assessmentSubjectService.updateOrCreate(oasysAssessment.assessment, it)
    }

    return sessionRepository.save(
      Session(
        userSessionId = request.user.identifier,
        userDisplayName = request.user.displayName,
        userAccess = request.user.accessMode,
        oasysAssessment = oasysAssessment,
      ),
    ).let {
      log.info("Session created for OASys assessment PK: ${request.oasysAssessmentPk}")
      oasysAssessment.assessment.info?.let { assessmentInfo ->
        val (majorVersion, minorVersion) = assessmentInfo.formVersion.split(".")
        OneTimeLinkResponse("${applicationConfig.formBaseUrl}/sbna-poc/$majorVersion/$minorVersion/start?sessionId=${it.linkUuid}")
      } ?: OneTimeLinkResponse("${applicationConfig.formBaseUrl}/sbna-poc/start?sessionId=${it.linkUuid}")
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

    if (session.oasysAssessment.assessment.info == null) {
      assessmentFormInfoRepository.save(
        AssessmentFormInfo(
          formName = request.form,
          formVersion = request.version,
          assessment = session.oasysAssessment.assessment,
        ),
      )
    }

    return sessionRepository.save(session).let {
      log.info("Used one time link: ${it.linkUuid}")
      SessionResponse.from(it, it.oasysAssessment.assessment)
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
