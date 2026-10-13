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
import java.io.IOException
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

    val concurrencyLimit = 20
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
  // Network-level failures (e.g. HMPPS Auth resetting connections) are transient too, so get the same
  // treatment with a longer, increasing backoff. A failed attempt deletes anything it created in AAP.
  private suspend fun migrateWithRetry(assessment: Assessment): Context {
    repeat(MAX_ATTEMPTS - 1) { attempt ->
      try {
        return assessmentMigrator.migrate(assessment)
      } catch (e: Exception) {
        when {
          e.message?.contains(RETRYABLE_CONSTRAINT) == true -> {
            log.info("Retrying migration of assessment ${assessment.id} after a $RETRYABLE_CONSTRAINT collision (attempt ${attempt + 1})")
            delay(RETRY_DELAY_MS)
          }
          e.isTransientIoFailure() -> {
            log.info("Retrying migration of assessment ${assessment.id} after an I/O failure: ${e.message} (attempt ${attempt + 1})")
            delay(IO_RETRY_DELAY_MS * (attempt + 1))
          }
          else -> throw e
        }
      }
    }
    return assessmentMigrator.migrate(assessment)
  }

  private fun Throwable.isTransientIoFailure() = generateSequence(this) { it.cause }.any { it is IOException }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
    private const val RETRYABLE_CONSTRAINT = "uq_user_id_and_type"
    private const val MAX_ATTEMPTS = 3
    private const val RETRY_DELAY_MS = 250L
    private const val IO_RETRY_DELAY_MS = 2000L
  }
}
