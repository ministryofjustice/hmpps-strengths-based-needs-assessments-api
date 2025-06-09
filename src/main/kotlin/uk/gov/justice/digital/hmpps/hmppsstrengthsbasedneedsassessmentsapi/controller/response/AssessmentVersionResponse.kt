package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentVersionResponse(
  val uuid: UUID,
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime,
  var tag: Tag,
  val versionNumber: Int,
) {
  companion object {
    fun from(assessmentVersion: AssessmentVersion): AssessmentVersionResponse = with(assessmentVersion) {
      AssessmentVersionResponse(
        uuid,
        createdAt,
        updatedAt,
        tag,
        versionNumber,
      )
    }

    fun fromAll(assessmentVersions: List<AssessmentVersion>): List<AssessmentVersionResponse> = assessmentVersions.map(::from)
  }
}
