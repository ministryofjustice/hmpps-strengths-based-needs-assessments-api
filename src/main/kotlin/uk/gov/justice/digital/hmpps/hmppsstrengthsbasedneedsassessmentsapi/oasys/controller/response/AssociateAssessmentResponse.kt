package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import java.util.UUID

class AssociateAssessmentResponse(
  val sanAssessmentId: UUID,
  val sanAssessmentVersion: UUID,
  val sentencePlanId: UUID? = null,
  val sentencePlanVersion: UUID? = null,
) {
  companion object {
    fun from(assessmentId: UUID, assessmentVersionId: UUID): AssociateAssessmentResponse {
      return AssociateAssessmentResponse(
        assessmentId,
        assessmentVersionId,
      )
    }
  }
}
