package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.Value

class UpdateAssessmentAnswersCommand(
  override val user: UserDetails,
  override val timeline: Timeline? = null,
  val assessmentUuid: String,
  val added: Map<String, Value>,
  val removed: List<String>,
) : Requestable
