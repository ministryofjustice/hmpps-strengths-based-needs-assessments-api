package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails

sealed interface Requestable : Command {
  val user: UserDetails
}
