package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.AAPService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository

@Component
class AssessmentHistoryMigrator(
  private val aapService: AAPService,
  private val assessmentRepository: AssessmentRepository,
) {
  fun migrate(context: Context) {}

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
