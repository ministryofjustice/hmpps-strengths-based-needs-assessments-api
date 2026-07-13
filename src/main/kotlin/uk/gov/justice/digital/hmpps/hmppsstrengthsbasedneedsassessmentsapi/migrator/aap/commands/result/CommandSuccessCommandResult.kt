package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.result

data class CommandSuccessCommandResult(
  override val message: String = "Done",
) : CommandResult {
  override val success: Boolean = true
}
