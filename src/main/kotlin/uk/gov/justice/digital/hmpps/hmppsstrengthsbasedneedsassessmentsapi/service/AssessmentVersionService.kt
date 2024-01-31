package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.*

@Service
class AssessmentVersionService(
  val assessmentService: AssessmentService,
  val assessmentVersionRepository: AssessmentVersionRepository,
  val dataMappingService: DataMappingService,
) {
  fun create(tag: String, assessment: Assessment): AssessmentVersion {
    return assessmentVersionRepository.save(
      AssessmentVersion(
        assessment = assessment,
        tag = tag,
        answers = emptyMap(),
      ),
    ).also { log.info("Created assessment version with UUID ${it.uuid} and tag ${it.tag} for assessment ${assessment.uuid}") }
  }

  fun cloneFromPrevious(tag: String, assessment: Assessment): AssessmentVersion {
    val previousVersion = find(tag, assessment)

    return assessmentVersionRepository.save(
      AssessmentVersion(
        assessment = assessment,
        tag = tag,
        answers = previousVersion?.answers.orEmpty(),
        oasys_equivalent = previousVersion?.oasys_equivalent.orEmpty(),
      ),
    ).also { log.info("Cloned assessment version with UUID ${it.uuid} and tag ${it.tag} for assessment ${assessment.uuid}") }
  }

  fun findOrCreate(tag: String, assessment: Assessment): AssessmentVersion {
    return assessmentVersionRepository.findByAssessmentUuidAndTag(assessment.uuid, tag).maxByOrNull { it.createdAt }
      ?: create(tag, assessment)
  }

  fun findOrCreateMany(tags: List<String>, assessment: Assessment): List<AssessmentVersion> {
    val assessmentVersions = assessmentVersionRepository.findByAssessmentUuid(assessment.uuid)
    return tags.map { tag ->
      assessmentVersions.filter { it.tag == tag }.maxByOrNull { it.createdAt }
        ?: create(tag, assessment)
    }
  }

  fun find(tag: String, assessment: Assessment): AssessmentVersion? {
    return assessmentVersionRepository.findByAssessmentUuid(assessment.uuid).maxByOrNull { it.createdAt }
  }

  fun updateAnswers(assessmentUuid: UUID, request: UpdateAssessmentAnswersDto) {
    log.info("Adding answers to assessment with UUID $assessmentUuid for tags ${request.tags}")
    assessmentService.findByUuid(assessmentUuid)?.let {
      findOrCreateMany(request.tags, it)
        .let { assessmentVersions ->
          assessmentVersions.forEach { assessmentVersion ->
            assessmentVersion.answers = assessmentVersion.answers.plus(request.answersToAdd)
              .filterNot { thisAnswer -> request.answersToRemove.contains(thisAnswer.key) }
            assessmentVersion.oasys_equivalent = dataMappingService.getOasysEquivalent(assessmentVersion)

            assessmentVersionRepository.save(assessmentVersion)
          }
        }
    }
  }

  fun getAnswers(assessmentUuid: UUID, tag: String): Answers {
    log.info("Getting answers for assessment with UUID $assessmentUuid for tag $tag")
    return assessmentService.findByUuid(assessmentUuid)?.let {
      find(tag, it)?.answers
    } ?: throw AssessmentNotFoundException("No assessment found with UUID $assessmentUuid")
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
