package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserLocation
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
  fun findByUuid(uuid: UUID): Assessment = assessmentRepository.findByUuid(uuid)
    ?: throw AssessmentNotFoundException("No assessment found with UUID $uuid")

  @Transactional
  fun create(userLocation: UserLocation?): Assessment = Assessment.new(formConfigProvider.getLatest(), userLocation ?: UserLocation.PRISON) // TODO should be defaulted to COMMUNITY not PRISON - just set this way around for testing
    .apply { assessmentVersions.forEach { assessmentVersionService.setOasysEquivalents(it) } }
    .run(assessmentRepository::save)
    .also { log.info("Created assessment with UUID ${it.uuid}") }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
