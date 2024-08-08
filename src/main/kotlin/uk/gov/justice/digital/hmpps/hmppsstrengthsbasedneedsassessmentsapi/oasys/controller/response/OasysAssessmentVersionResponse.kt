package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import java.time.LocalDateTime
import java.util.UUID

data class OasysAssessmentVersionResponse(
  val sanAssessmentId: UUID,
  val sanAssessmentVersion: Int,
  val sanAssessmentData: AssessmentResponse,
  val lastUpdatedTimestamp: LocalDateTime,
) {
  companion object {
    fun from(assessmentVersion: AssessmentVersion): OasysAssessmentVersionResponse {
      return OasysAssessmentVersionResponse(
        assessmentVersion.assessment.uuid,
        assessmentVersion.versionNumber,
        AssessmentResponse.from(assessmentVersion),
        assessmentVersion.updatedAt,
      )
    }
  }
}
