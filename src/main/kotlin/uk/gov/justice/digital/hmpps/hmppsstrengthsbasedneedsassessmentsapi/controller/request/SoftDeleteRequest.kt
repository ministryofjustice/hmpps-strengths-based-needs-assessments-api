package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.PositiveOrZero
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import java.util.UUID

class SoftDeleteRequest(
  @Schema(description = "The Assessment version number (inclusive) to delete from.", example = "1")
  @PositiveOrZero
  val versionFrom: Int,
  @Schema(description = "The Assessment version number (exclusive) to delete to.", example = "2")
  @PositiveOrZero
  val versionTo: Int? = null,
  @Schema(description = "User")
  @Valid
  override val userDetails: UserDetails,
) : AuditableRequest {
  fun toAssessmentVersionCriteria(assessmentUuid: UUID) =
    AssessmentVersionCriteria(assessmentUuid, versionNumberFrom = versionFrom, versionNumberTo = versionTo)
}
