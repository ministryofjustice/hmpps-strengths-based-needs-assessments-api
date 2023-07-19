package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentId: String): Assessment {
    return assessmentRepository.save(Assessment(oasysAssessmentId = oasysAssessmentId))
      .also { log.info("Created assessment for OASys assessment ID: ${it.oasysAssessmentId}") }
  }

  fun findOrCreateAssessment(oasysAssessmentId: String): Assessment {
    return assessmentRepository.findByOasysAssessmentId(oasysAssessmentId)
      ?: createAssessmentWithOasysId(oasysAssessmentId)
  }

  fun addAnswers(assessmentUuid: UUID, answers: Answers) {
    log.info("Adding answers to assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        it.answers = it.answers.plus(answers)
        assessmentRepository.save(it)
      }
      ?: throw AssessmentNotFoundException("Not assessment found with ID $assessmentUuid")
  }

  fun getAnswers(assessmentUuid: UUID): Answers {
    log.info("Getting answers for assessment with ID $assessmentUuid")
    return assessmentRepository.findByUuid(assessmentUuid)?.answers
      ?: throw AssessmentNotFoundException("Not assessment found with ID $assessmentUuid")
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
