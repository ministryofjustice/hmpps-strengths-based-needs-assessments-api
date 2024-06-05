package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag

class CounterSignAssessmentRequest(
  @Schema(description = "The SAN Assessment version number that was returned from the Sign Assessment API call.", example = "2")
  val sanVersionNumber: Long,
  @Schema(description = "OASys user identifier of the counter-signing practitioner.", example = "111111")
  val counterSignerID: String,
  @Schema(description = "Forename and Surname of counter-signing practitioner.", example = "John Doe")
  val counterSignerName: String,
  @Schema(description = "Indicates what type of case this is")
  val outcome: Tag,
)
