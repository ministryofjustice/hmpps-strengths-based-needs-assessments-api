package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentMetadata(
  val uuid: UUID,
  val createdAt: LocalDateTime,
  val versionUuid: UUID,
  val versionNumber: Int,
  val versionCreatedAt: LocalDateTime,
  val versionUpdatedAt: LocalDateTime,
  val versionTag: Tag,
  val formVersion: String?,
)

data class AssessmentResponse(
  val metaData: AssessmentMetadata,
  val assessment: Answers,
  val oasysEquivalent: OasysEquivalent,
) {
  companion object {
    fun from(assessmentVersion: AssessmentVersion): AssessmentResponse {
      return AssessmentResponse(
        AssessmentMetadata(
          assessmentVersion.assessment.uuid,
          assessmentVersion.assessment.createdAt,
          assessmentVersion.uuid,
          assessmentVersion.versionNumber,
          assessmentVersion.createdAt,
          assessmentVersion.updatedAt,
          assessmentVersion.tag,
          assessmentVersion.assessment.info?.formVersion,
        ),
        assessmentVersion.answers,
        assessmentVersion.oasysEquivalents,
      )
    }
  }
}
