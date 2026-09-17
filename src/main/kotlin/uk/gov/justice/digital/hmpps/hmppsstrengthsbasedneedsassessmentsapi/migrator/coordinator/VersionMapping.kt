package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator

import java.time.LocalDateTime

data class VersionMapping(val version: Long, val createdAt: LocalDateTime, var event: String)
