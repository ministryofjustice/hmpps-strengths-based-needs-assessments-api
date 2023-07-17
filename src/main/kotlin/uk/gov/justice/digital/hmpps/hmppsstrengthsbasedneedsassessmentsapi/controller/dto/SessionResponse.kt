package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import java.util.UUID

data class SessionResponse(
  val uuid: UUID,
  val sessionId: String,
  val userDisplayName: String,
  val accessLevel: UserAccess,
  val assessmentUUID: UUID,
) {
  companion object {
    fun from(session: Session) = with(session) { SessionResponse(uuid, userSessionId, userDisplayName, userAccess, assessment.uuid) }
  }
}
