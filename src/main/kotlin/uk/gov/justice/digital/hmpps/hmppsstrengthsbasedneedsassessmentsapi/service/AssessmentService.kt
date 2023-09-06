package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.UpdateCollectionEntryRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@Service
class AssessmentService(
  val assessmentRepository: AssessmentRepository,
  val subjectService: SubjectService,
) {
  fun createAssessmentWithOasysId(oasysAssessmentId: String, crn: String): Assessment {
    val subject = subjectService.findOrCreateSubject(crn)
    return assessmentRepository.save(Assessment(oasysAssessmentId = oasysAssessmentId, answers = emptyMap(), subject = subject))
      .also { log.info("Created assessment for OASys assessment ID: ${it.oasysAssessmentId}") }
  }

  fun findOrCreateAssessment(oasysAssessmentId: String, crn: String): Assessment {
    return assessmentRepository.findByOasysAssessmentId(oasysAssessmentId)
      ?: createAssessmentWithOasysId(oasysAssessmentId, crn)
  }

  fun updateAnswers(assessmentUuid: UUID, request: UpdateAssessmentAnswersDto) {
    log.info("Adding answers to assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        it.answers = it.answers.plus(request.answersToAdd)
          .filterNot { thisAnswer -> request.answersToRemove.contains(thisAnswer.key) }

        assessmentRepository.save(it)
      }
      ?: throw AssessmentNotFoundException("No assessment found with ID $assessmentUuid")
  }

  fun addEntryToCollection(assessmentUuid: UUID, collectionName: String, request: UpdateAssessmentAnswersDto) {
    log.info("Adding answers to collection '$collectionName' for assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        val answer = it.answers[collectionName] ?: Answer(type = AnswerType.COLLECTION, description = "", collection = emptyList(), options = null, value = null, values = null)
        val updatedCollection = answer.collection?.plus(request.answersToAdd)
        answer.collection = updatedCollection

        it.answers = it.answers.plus(collectionName to answer)

        assessmentRepository.save(it)
      }
      ?: throw AssessmentNotFoundException("No assessment found with ID $assessmentUuid")
  }

  fun updateEntryInCollection(assessmentUuid: UUID, collectionName: String, request: UpdateCollectionEntryRequest) {
    log.info("Updating answers in collection '$collectionName' for assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        val answer = it.answers[collectionName] ?: Answer(type = AnswerType.COLLECTION, description = "", collection = emptyList(), options = null, value = null, values = null)
        val updatedCollection = answer.collection
          .orEmpty()
          .toMutableList()

        updatedCollection[request.index] = request.answers.answersToAdd
        answer.collection = updatedCollection

        it.answers = it.answers.plus(collectionName to answer)

        assessmentRepository.save(it)
      }
      ?: throw AssessmentNotFoundException("No assessment found with ID $assessmentUuid")
  }

  fun removeEntryFromCollection(assessmentUuid: UUID, collectionName: String, indexToRemove: Int) {
    log.info("Removing entry from collection '$collectionName' at index '$indexToRemove' for assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        val answer = it.answers[collectionName] ?: Answer(type = AnswerType.COLLECTION, description = "", collection = emptyList(), options = null, value = null, values = null)

        val updatedCollection = answer.collection.orEmpty().toMutableList()
        updatedCollection.removeAt(indexToRemove)

        answer.collection = updatedCollection

        it.answers = it.answers.plus(collectionName to answer)

        assessmentRepository.save(it)
      }
      ?: throw AssessmentNotFoundException("No assessment found with ID $assessmentUuid")
  }

  fun getEntryFromCollection(assessmentUuid: UUID, collectionName: String, indexToReturn: Int): Answers {
    log.info("Get entry from collection '$collectionName' at index '$indexToReturn' for assessment with ID $assessmentUuid")
    assessmentRepository.findByUuid(assessmentUuid)
      ?.let {
        val answer = it.answers[collectionName] ?: Answer(type = AnswerType.COLLECTION, description = "", collection = emptyList(), options = null, value = null, values = null)

        return answer.collection
          ?.elementAtOrNull(indexToReturn) ?: throw AssessmentNotFoundException("No entry found at index '$indexToReturn' for collection '$collectionName'")
      }
      ?: throw AssessmentNotFoundException("No assessment found with ID $assessmentUuid")
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
