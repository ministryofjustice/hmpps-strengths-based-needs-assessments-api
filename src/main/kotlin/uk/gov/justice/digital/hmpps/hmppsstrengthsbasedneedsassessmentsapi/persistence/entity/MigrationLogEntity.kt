package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity(name = "MigrationLog")
@Table(name = "migration_log")
class MigrationLogEntity(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  var id: Long? = null,

  @Column(name = "entity_type")
  val entityType: String,

  @Column(name = "entity_id", nullable = true)
  val entityId: Long? = null,

  @Column(name = "entity_uuid", nullable = true)
  val entityUuid: UUID? = null,

  @Column(name = "aap_uuid", nullable = true)
  val aapUuid: UUID? = null,

  @Column(name = "migrated_at")
  val migratedAt: LocalDateTime = LocalDateTime.now(),
)
