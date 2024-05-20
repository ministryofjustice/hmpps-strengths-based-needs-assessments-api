package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
  val formConfigProvider: FormConfigProvider,
  val assessmentVersionService: AssessmentVersionService,
) {
  fun findByUuid(uuid: UUID): Assessment {
    return assessmentRepository.findByUuid(uuid)
      ?: throw AssessmentNotFoundException("No assessment found with UUID $uuid")
  }

  fun create(): Assessment {
    return Assessment.new(formConfigProvider.getLatest())
      .apply { assessmentVersions.forEach { assessmentVersionService.setOasysEquivalent(it) } }
      .also { assessmentRepository.save(it).run { log.info("Created assessment with UUID $uuid") } }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
