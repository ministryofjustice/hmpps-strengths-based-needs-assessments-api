package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.IdentifierType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.Value

class CreateAssessmentCommand(
  override val user: UserDetails,
  override val timeline: Timeline? = null,
  val formVersion: String,
  val assessmentType: String,
  val identifiers: Map<IdentifierType, String>? = null,
  val properties: Map<String, Value>? = null,
  val flags: List<String> = emptyList(),
) : Requestable
