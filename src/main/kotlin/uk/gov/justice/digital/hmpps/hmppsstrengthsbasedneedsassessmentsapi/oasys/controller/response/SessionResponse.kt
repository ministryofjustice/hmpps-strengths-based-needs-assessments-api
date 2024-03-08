package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.UserAccess
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
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
