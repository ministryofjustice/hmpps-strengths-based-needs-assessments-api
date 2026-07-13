package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import java.time.LocalDateTime

class CreateTimelineItemCommand(
  val timestamp: LocalDateTime,
  override val user: UserDetails,
  override val timeline: Timeline,
  val assessmentUuid: String,
) : Requestable,
  Resolvable {
  override fun resolve(
    commands: List<Requestable>,
  ): Requestable = this
}
