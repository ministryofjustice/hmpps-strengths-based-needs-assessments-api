package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.CreateSession
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.OneTimeLinkResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.SessionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.SessionRepository
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
    ).let { OneTimeLinkResponse.from(it) }
  }

  fun useOneTimeLink(uuid: UUID): SessionResponse? {
    return sessionRepository.findByLinkUuidAndLinkStatus(uuid, LinkStatus.UNUSED)?.let {
      it.linkStatus = LinkStatus.USED
      sessionRepository.save(it)

      SessionResponse.from(it)
    }
  }
}
