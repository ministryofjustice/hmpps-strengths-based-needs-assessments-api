package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
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

  fun create(): Assessment {
    return Assessment.new(formConfigProvider.getLatest())
      .apply { assessmentVersions.forEach { assessmentVersionService.setOasysEquivalents(it) } }
      .run(assessmentRepository::save)
      .also { log.info("Created assessment with UUID ${it.uuid}") }
  }

  @Transactional
  fun createAndAudit(userDetails: UserDetails): Assessment {
    return create().also {
      AssessmentVersionAudit(
        assessmentVersion = it.assessmentVersions.first(),
        userDetails = userDetails,
      ).run(assessmentVersionAuditRepository::save)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
