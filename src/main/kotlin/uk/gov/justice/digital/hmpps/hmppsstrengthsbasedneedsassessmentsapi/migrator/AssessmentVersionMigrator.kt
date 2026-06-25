package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.coordinator.VersionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.MigrationLogRepository
import java.time.LocalDateTime

@Component
class AssessmentVersionMigrator(
  private val aapService: AAPService,
  private val migrationLogRepository: MigrationLogRepository,
) {
  fun migrate(context: Context, assessmentVersion: AssessmentVersion, versionUpdatedAt: LocalDateTime): VersionMapping {
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
