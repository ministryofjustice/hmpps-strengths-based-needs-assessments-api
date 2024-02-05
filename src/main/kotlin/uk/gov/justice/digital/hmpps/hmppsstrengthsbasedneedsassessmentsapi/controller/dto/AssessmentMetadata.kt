package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

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
