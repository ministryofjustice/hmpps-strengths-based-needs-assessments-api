package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import java.util.UUID

data class SessionResponse(
  val uuid: UUID,
  val sessionId: String,
  val userDisplayName: String,
  val accessLevel: UserAccess,
  val assessmentUUID: UUID?,
  val oasysAssessmentPk: String?,
) {
  companion object {
    fun from(session: Session, assessment: Assessment) = with(session) { SessionResponse(uuid, userSessionId, userDisplayName, userAccess, assessment.uuid, oasysAssessment.oasysAssessmentPk) }
  }
}
