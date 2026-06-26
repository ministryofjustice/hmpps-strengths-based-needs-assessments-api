package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.runner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.AssessmentMigrator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.Stats
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
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
              val context = assessmentMigrator.migrate(assessment)

              synchronized(Stats) {
                Stats.numberOfAssessments += 1
                Stats.numberOfVersions += context.versionsMigrated
                Stats.numberOfCommands += context.migrationCommands
              }
            } catch (e: Exception) {
              log.warn("Failed to migrate ${assessment.id}: ${e.stackTraceToString()}")
              failedAssessments[assessment.id!!] = e.message ?: "Unknown error"
            }
          }
        }
      }

      jobs.awaitAll()
    }

    log.info("Finished migration in ${Stats.getDuration().toMinutes()} minutes")
    log.info("Migrated ${Stats.numberOfAssessments} assessments totalling ${Stats.numberOfVersions} versions and created ${Stats.numberOfCommands} events")

    log.info("Failed to migrate ${failedAssessments.size} assessments")
    failedAssessments.forEach { (assessmentId, message) ->
      log.error("Failed to migrate assessment $assessmentId: $message")
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
