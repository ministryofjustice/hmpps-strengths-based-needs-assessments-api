package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import java.util.UUID

data class CreateAssessmentResponse(
  val id: UUID,
  val version: Long,
) {
  companion object {
    fun from(assessment: Assessment): CreateAssessmentResponse {
      return CreateAssessmentResponse(
        id = assessment.uuid,
        version = assessment.assessmentVersions.first().versionNumber.toLong(),
      )
    }
  }
}
