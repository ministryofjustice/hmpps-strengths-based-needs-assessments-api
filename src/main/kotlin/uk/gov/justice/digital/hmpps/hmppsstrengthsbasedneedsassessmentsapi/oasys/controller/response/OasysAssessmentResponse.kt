package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import java.util.UUID

data class OasysAssessmentResponse(
  val sanAssessmentId: UUID,
  val sanAssessmentVersion: Int,
  val sentencePlanId: UUID? = null,
  val sentencePlanVersion: Int? = null,
) {
  companion object {
    fun from(assessmentVersion: AssessmentVersion): OasysAssessmentResponse {
      return OasysAssessmentResponse(
        assessmentVersion.assessment.uuid,
        assessmentVersion.versionNumber,
      )
    }
  }
}
