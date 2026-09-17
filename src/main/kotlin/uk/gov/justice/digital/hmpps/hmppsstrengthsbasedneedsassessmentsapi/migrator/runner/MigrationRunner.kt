package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.runner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.AssessmentMigrator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.Context
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.Stats
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import java.util.concurrent.ConcurrentHashMap

@Component
class MigrationRunner(
  private val assessmentRepository: AssessmentRepository,
  private val assessmentMigrator: AssessmentMigrator,
  private val aapService: AAPService,
) {
  fun run(assessmentIds: List<Long>?) = runBlocking {
    log.info("Priming auth")
    aapService.primeAuthToken()

    log.info("Starting migration")
    log.info("Migrating assessments: ${assessmentIds?.joinToString() ?: "All"}")

    Stats.start()

    val failedAssessments = ConcurrentHashMap<Long, String>()

    val concurrencyLimit = 25
    val semaphore = Semaphore(concurrencyLimit)

    val pageSize = 50
    var hasNext = true
    var totalPages: Int? = null
    var pageNumber = 0

    while (hasNext) {
      val pageRequest = PageRequest.of(0, pageSize, Sort.by("id").ascending())
      val page = when {
        !assessmentIds.isNullOrEmpty() -> assessmentRepository.findAllToMigrateById(assessmentIds, pageRequest)
        failedAssessments.isNotEmpty() -> assessmentRepository.findAllToMigrateExcludingIds(
          failedAssessments.keys,
          pageRequest,
        )

        else -> assessmentRepository.findAllToMigrate(pageRequest)
      }

      if (totalPages == null) {
        totalPages = page.totalPages
      }

      hasNext = page.hasNext()
      if (!page.hasContent()) break

      log.info("Migrating batch of ${page.content.size} items in page ${++pageNumber} of $totalPages")

      val jobs = page.content.map { assessment ->
        async(Dispatchers.IO) {
          semaphore.withPermit {
            try {
              val context = migrateWithRetry(assessment)

              synchronized(Stats) {
                Stats.numberOfAssessments += 1
                Stats.numberOfVersions += context.versionsMigrated
                Stats.numberOfCommands += context.migrationCommands
              }
            } catch (e: Exception) {
              log.warn("Failed to migrate ${assessment.id}: ${e.stackTraceToString()}")
              failedAssessments[assessment.id!!] = e.message ?: ("Stack: " + e.stackTraceToString())
            }
          }
        }
      }

      jobs.awaitAll()
    }

    log.info("Finished migration in ${Stats.getDuration().toMinutes()} minutes")
    log.info("Migrated ${Stats.numberOfAssessments} assessments totalling ${Stats.numberOfVersions} versions and created ${Stats.numberOfCommands} events")

    log.info("Failed to migrate ${failedAssessments.size} assessments")
    failedAssessments.forEach { (assessmentUuid, message) ->
      log.error("Failed to migrate assessment $assessmentUuid: $message")
    }
  }

  // Concurrent migrations can race to create the same OASys user's user_details row (only one
  // wins); the loser just needs to retry once the winner's insert has committed.
  private suspend fun migrateWithRetry(assessment: Assessment): Context {
    repeat(MAX_ATTEMPTS - 1) { attempt ->
      try {
        return assessmentMigrator.migrate(assessment)
      } catch (e: Exception) {
        if (e.message?.contains(RETRYABLE_CONSTRAINT) != true) throw e
        log.info("Retrying migration of assessment ${assessment.id} after a $RETRYABLE_CONSTRAINT collision (attempt ${attempt + 1})")
        delay(RETRY_DELAY_MS)
      }
    }
    return assessmentMigrator.migrate(assessment)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
    private const val RETRYABLE_CONSTRAINT = "uq_user_id_and_type"
    private const val MAX_ATTEMPTS = 2
    private const val RETRY_DELAY_MS = 250L
  }
}
