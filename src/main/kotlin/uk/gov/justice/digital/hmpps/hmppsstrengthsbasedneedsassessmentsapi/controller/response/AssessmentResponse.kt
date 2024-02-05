package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentMetadata(
  val uuid: UUID,
  val createdAt: LocalDateTime,
  val oasys_pks: List<String>,
  val versionUuid: UUID,
  val versionCreatedAt: LocalDateTime,
  val versionTag: String,
  val formVersion: String,
)

data class AssessmentResponse(
  val metaData: AssessmentMetadata,
  val assessment: Answers,
  val oasysEquivalent: OasysEquivalent,
) {
  constructor(assessmentVersion: AssessmentVersion) : this(
    AssessmentMetadata(
      assessmentVersion.assessment!!.uuid,
      assessmentVersion.assessment.createdAt,
      assessmentVersion.assessment.oasysAssessments.map { it.oasysAssessmentPk },
      assessmentVersion.uuid,
      assessmentVersion.createdAt,
      assessmentVersion.tag,
      assessmentVersion.assessment.info!!.formVersion,
    ),
    assessmentVersion.answers,
    assessmentVersion.oasys_equivalent,
  )
}
