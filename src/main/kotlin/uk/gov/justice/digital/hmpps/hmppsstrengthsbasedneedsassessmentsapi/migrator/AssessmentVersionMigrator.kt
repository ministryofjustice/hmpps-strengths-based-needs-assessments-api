package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.AddCollectionItemCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.CreateCollectionCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.RemoveCollectionItemCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.Requestable
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.Resolvable
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.UpdateAssessmentAnswersCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.UpdateAssessmentPropertiesCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.request.CommandResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.result.AddCollectionItemCommandResult
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.result.CreateCollectionCommandResult
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.AuthSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator.VersionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.mappers.AnswerMapper
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.MigrationLogEntity
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.MigrationLogRepository
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID.fromString

@Component
class AssessmentVersionMigrator(
  private val aapService: AAPService,
  private val migrationLogRepository: MigrationLogRepository,
) {
  fun migrate(context: Context, assessmentVersion: AssessmentVersion, versionUpdatedAt: LocalDateTime): VersionMapping {
    val migrationUser = UserDetails("MIGRATION_USER", "Migration User", AuthSource.NOT_SPECIFIED)
    val answers = assessmentVersion.answers
      .entries
      .filter {
        !AnswerMapper.isProperty(it)
          && !AnswerMapper.isCollection(it)
          && it.value != context.previousAnswers[it.key] // The answer has changed since the previous version
      }

    val updateAnswersCommand = answers
      .map { AnswerMapper.toAapValue(it) }
      .let { convertedAnswers ->
        UpdateAssessmentAnswersCommand(
          user = migrationUser,
          added = convertedAnswers.toMap(),
          timeline = null,
          assessmentUuid = context.assessmentUuid,
          removed = (context.previousAnswers.keys - answers.map { a -> a.key }.toSet()).toList(), // Removing all current answers from previous, what keys are left from previous answers have been removed
        )
      }

    val properties = assessmentVersion.answers
      .entries
      .filter {
        AnswerMapper.isProperty(it)
          && !AnswerMapper.isCollection(it)
          && it.value != context.previousProperties[it.key] // The property has changed since the previous version
      }

    val updatePropertiesCommand = properties
      .map { AnswerMapper.toAapValue(it) }
      .let {
        UpdateAssessmentPropertiesCommand(
          user = migrationUser,
          added = it.toMap(),
          timeline = null,
          assessmentUuid = context.assessmentUuid,
          removed = (context.previousProperties.keys -  properties.map { a -> a.key }.toSet()).toList(), // Removing all current answers from previous, what keys are left from previous properties have been removed
        )
      }

    val victimsToRemove = context.victimsCollection.map { victimId ->
      RemoveCollectionItemCommand(
        user = migrationUser,
        assessmentUuid = context.assessmentUuid,
        collectionItemUuid = victimId,
      )
    }

    val victimsToAdd = assessmentVersion.answers
      .filter { AnswerMapper.isCollection(it) && it.key == "victims" }
      .flatMap { (_, victims) ->
        victims.collection?.map { victim ->
          AddCollectionItemCommand(
            user = migrationUser,
            collectionUuid = context.victimsCollectionUuid,
            answers = victim.entries.filter { !AnswerMapper.isProperty(it) }.associate { AnswerMapper.toAapValue(it) },
            properties = victim.entries.filter { AnswerMapper.isProperty(it) }
              .associate { AnswerMapper.toAapValue(it) },
            index = null,
            timeline = null,
            assessmentUuid = context.assessmentUuid,
          )
        }.orEmpty()
      }

    val commands: List<Requestable> = listOf(
      updateAnswersCommand,
      updatePropertiesCommand,
      *victimsToRemove.toTypedArray(),
      *victimsToAdd.toTypedArray(),

      ).fold(emptyList()) { resolved, command ->
      resolved + (if (command is Resolvable) command.resolve(resolved) else command)
    }

    context.previousAnswers = assessmentVersion.answers

    if (assessmentVersion.versionNumber < context.previousVersion) {
      throw IllegalStateException("Attempting to process events out of order for ${assessmentVersion.assessment.uuid}: ${assessmentVersion.versionNumber} after ${context.previousVersion}")
    }

    if (commands.isNotEmpty()) {
      log.info("Dispatching ${commands.size} commands for plan ${context.assessment.id} version ${assessmentVersion.versionNumber}")
      val requestStarted = LocalDateTime.now()
      val response = aapService.dispatchCommands(
        versionUpdatedAt,
        commands,
      )
      context.migrationCommands += commands.size
      val requestDuration = Duration.between(requestStarted, LocalDateTime.now())
      log.info("${commands.size} executed in ${requestDuration.toMillis()} ms")
      context.previousVersion = assessmentVersion.versionNumber
      response.commands.zip(commands).forEach { (response: CommandResponse, request: Requestable) ->
        when (response.result) {
          is AddCollectionItemCommandResult -> when {
            (request as AddCollectionItemCommand).collectionUuid == context.victimsCollectionUuid -> {
              val collectionItemUuid = response.result.collectionItemUuid
              context.victimsCollection.add(response.result.collectionItemUuid)
              migrationLogRepository.save(
                MigrationLogEntity(
                  entityType = "GOAL",
                  entityUuid = fromString(collectionItemUuid),
                  aapUuid = fromString(response.result.collectionItemUuid),
                ),
              )
            }
          }

          is CreateCollectionCommandResult -> when ((request as CreateCollectionCommand).name) {
            "VICTIMS" -> {
              migrationLogRepository.save(
                MigrationLogEntity(
                  entityType = "VICTIMS",
                  aapUuid = fromString(response.result.collectionUuid),
                ),
              )
            }
          }

          else -> {}
        }
      }
    }

    return VersionMapping(
      assessmentVersion.versionNumber.toLong(),
      assessmentVersion.updatedAt,
      assessmentVersion.tag.name,
    )
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
