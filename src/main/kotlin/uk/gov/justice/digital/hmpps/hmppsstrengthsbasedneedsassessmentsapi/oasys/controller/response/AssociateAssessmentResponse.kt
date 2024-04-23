package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import java.util.UUID

class AssociateAssessmentResponse(
  val sanAssessmentId: UUID,
  val sanAssessmentVersion: Long,
  val sentencePlanId: UUID? = null,
  val sentencePlanVersion: Long? = null,
) {
  companion object {
    fun from(assessmentId: UUID, assessmentVersionNumber: Long): AssociateAssessmentResponse {
      return AssociateAssessmentResponse(
        assessmentId,
        assessmentVersionNumber,
      )
    }
  }
}
