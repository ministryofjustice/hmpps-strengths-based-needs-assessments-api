package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.UserAccess

data class UserInformation(
  @Schema(required = true, description = "User identifier", example = "ABC12345678")
  val identifier: String,
  @Schema(required = true, description = "User display name", example = "Probation User")
  val displayName: String,
  @Schema(required = true, description = "User access mode", example = "READ_WRITE")
  val accessMode: UserAccess,
  @Schema(required = true, description = "URL to return the user back to OASys", example = "https://foo.bar/return/url")
  val returnUrl: String? = null,
)

class CreateOneTimeLinkRequest(
  @Schema(required = true, description = "OASys assessment ID", example = "1234567890")
  val oasysAssessmentPk: String,
  @Schema(required = true, description = "Version of the assessment to view/edit", example = "1")
  val assessmentVersion: Long? = 0,
  @Schema(required = true, description = "User information")
  val user: UserInformation,
  @Schema(description = "Assessment subject details")
  val subjectDetails: SubjectDetailsRequest? = null,
)
