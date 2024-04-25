package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.UserInformation
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.Session
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import java.util.UUID

data class UserSessionResponse(
  val uuid: UUID,
  val assessmentUUID: UUID?,
  val oasysAssessmentPk: String?,
  val user: UserInformation,
) {
  companion object {
    fun from(session: Session, assessment: Assessment) = with(session) {
      UserSessionResponse(
        uuid,
        assessment.uuid,
        oasysAssessment.oasysAssessmentPk,
        UserInformation(
          userSessionId,
          userDisplayName,
          userAccess,
        ),
      )
    }
  }
}
