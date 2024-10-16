package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
  val assessmentVersionAuditRepository: AssessmentVersionAuditRepository,
  val formConfigProvider: FormConfigProvider,
  val assessmentVersionService: AssessmentVersionService,
) {
  fun findByUuid(uuid: UUID): Assessment {
    return assessmentRepository.findByUuid(uuid)
      ?: throw AssessmentNotFoundException("No assessment found with UUID $uuid")
  }

  @Transactional
  fun create(): Assessment {
    return Assessment.new(formConfigProvider.getLatest())
      .apply { assessmentVersions.forEach { assessmentVersionService.setOasysEquivalents(it) } }
      .run(assessmentRepository::save)
      .also { log.info("Created assessment with UUID ${it.uuid}") }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
