package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

interface Resolvable {
  fun resolve(commands: List<Requestable>): Requestable
}
