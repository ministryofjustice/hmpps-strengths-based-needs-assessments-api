package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClientResponseException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.CreateAssessmentCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.CreateCollectionCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.Timeline
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.result.CreateAssessmentCommandResult
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.result.CreateCollectionCommandResult
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.AuthSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator.CoordinatorService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator.MigrateAssociationRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator.VersionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.MigrationLogEntity
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.MigrationLogRepository
import java.time.LocalDateTime
import java.util.UUID

@Component
class AssessmentMigrator(
  private val assessmentVersionRepository: AssessmentVersionRepository,
  private val assessmentVersionMigrator: AssessmentVersionMigrator,
  private val assessmentHistoryMigrator: AssessmentHistoryMigrator,
  private val aapService: AAPService,
  private val coordinatorService: CoordinatorService,
  private val migrationLogRepository: MigrationLogRepository,
) {
  @Transactional
  fun migrate(assessment: Assessment): Context {
    log.info("Migrating assessment ${assessment.id}")

    val versions = assessment.uuid
      .let(assessmentVersionRepository::findAllByAssessmentUuid)

    val creatingUser = versions
      .minByOrNull { it.createdAt }
      ?.assessmentVersionAudit
      ?.firstOrNull()
      ?.userDetails
      ?.run(UserDetails::from)
      ?: UserDetails("UNKNOWN_USER", "Unknown User", AuthSource.NOT_SPECIFIED)
        .also { log.info("Defaulting creating user for assessment: ${assessment.id}") }

    val context = createContext(assessment, creatingUser)

    var versionMappings: List<VersionMapping>

    try {
      val associatedVersions: Map<LocalDateTime, AssessmentVersion> = versions
        .filter { !it.deleted }
        .groupBy { it.versionNumber }
        .map { it.key to it.value.maxBy { version -> version.updatedAt } }.toMap()
        .values
        .sortedBy { it.versionNumber }
        .associateBy { current ->
          // versions *should* not have been created before the assessment, but let's ensure we handle it regardless
          maxOf(current.updatedAt, assessment.createdAt.plusSeconds(1))
        }

      versionMappings = associatedVersions.map { (updatedAt, version) ->
        val updatingUser = version.assessmentVersionAudit.firstOrNull()?.userDetails?.run(UserDetails::from)
          ?: UserDetails("UNKNOWN_USER", "Unknown User", AuthSource.NOT_SPECIFIED)
            .also { log.info("Defaulting updating user for assessment: ${assessment.id}") }
        assessmentVersionMigrator
          .migrate(context, version, updatedAt, updatingUser)
          .also { context.versionsMigrated += 1 }
      }

      assessmentHistoryMigrator.migrate(context)

      runCatching {
        coordinatorService.migrateAssociations(
          MigrateAssociationRequest(
            mappings = versionMappings
              .mapNotNull { mapping ->
                when (mapping.event) {
                  "LOCKED_INCOMPLETE" -> mapping.apply { event = "LOCKED" }
                  "UNSIGNED" -> null
                  else -> mapping
                }
              },
            entityUuidFrom = assessment.uuid,
            entityUuidTo = UUID.fromString(context.assessmentUuid),
          ),
        )
      }.onFailure { ex ->
        when (ex) {
          is WebClientResponseException.NotFound -> throw IllegalStateException("Association not found for assessment ${assessment.uuid}")
          else -> throw ex
        }
      }

      migrationLogRepository.save(
        MigrationLogEntity(
          entityType = "AAP_SAN",
          entityId = assessment.id,
          entityUuid = assessment.uuid,
          aapUuid = UUID.fromString(context.assessmentUuid),
        ),
      ).also { log.info("Migrated assessment ${assessment.id}") }
    } catch (e: Exception) {
      log.warn("Failed to migrate assessment ${assessment.id}: ${e.message}")
      aapService.deleteAssessment(UUID.fromString(context.assessmentUuid))
      throw e
    }

    return context
  }

  fun createContext(assessment: Assessment, creatingUser: UserDetails): Context {
//    val creatingUser = UserDetails("UNKNOWN_USER", "Unknown User", AuthSource.NOT_SPECIFIED)
    val commands = listOf(
      CreateAssessmentCommand(
        user = creatingUser,
        formVersion = "v1.0",
        properties = emptyMap(),
        assessmentType = "STRENGTHS_AND_NEEDS",
        // TODO: verify if we can get this elsewhere, looks like the subject table was removed
        // identifiers = assessment.crn?.let { mapOf(IdentifierType.CRN to it) },
        identifiers = emptyMap(),
        timeline = Timeline(
          "MIGRATED_SAN",
          mapOf(
            "date" to LocalDateTime.now(),
          ),
        ),
        flags = listOf("SAN_BETA"),
      ),
      CreateCollectionCommand(
        name = "VICTIMS",
        parentCollectionItemUuid = null,
        user = creatingUser,
        assessmentUuid = "@0",
      ),
    )

    val response =
      aapService.dispatchCommands(
        assessment.createdAt,
        commands = commands,
      )

    return Context(
      assessment = assessment,
      assessmentUuid = response.extractNthInstance<CreateAssessmentCommandResult>(0).assessmentUuid,
      victimsCollectionUuid = response.extractNthInstance<CreateCollectionCommandResult>(0).collectionUuid,
      migrationCommands = commands.size,
    )
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
