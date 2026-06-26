package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator

import java.util.UUID

data class MigrateAssociationRequest(
  val mappings: List<VersionMapping>,
  val entityUuidFrom: UUID,
  val entityUuidTo: UUID,
  // TODO: Confirm these types
  val entityTypeFrom: String = "SAN",
  val entityTypeTo: String = "AAP_SAN",
)
