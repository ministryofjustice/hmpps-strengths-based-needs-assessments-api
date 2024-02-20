package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import java.util.UUID

@Service
class AssessmentVersionService(
  val assessmentService: AssessmentService,
  val assessmentVersionRepository: AssessmentVersionRepository,
  val dataMappingService: DataMappingService,
) {
  fun create(tag: String, assessment: Assessment): AssessmentVersion {
    val assessmentVersion = AssessmentVersion(
      tag = tag,
      answers = emptyMap(),
      assessment = assessment,
    )

    return assessmentVersionRepository.save(assessmentVersion).also { log.info("Created assessment version with UUID ${it.uuid} and tag ${it.tag} for assessment ${assessment.uuid}") }
  }

  fun cloneFromPrevious(tag: String, assessment: Assessment): AssessmentVersion {
    val cloneAssessmentVersion = AssessmentVersion(
      tag = tag,
      assessment = assessment,
    )

    try {
      val previousVersion = find(AssessmentVersionCriteria(assessment.uuid, tag))
      cloneAssessmentVersion.answers = previousVersion.answers
      log.info("Cloned from assessment version UUID ${previousVersion.uuid}")
    } catch (_: AssessmentVersionNotFoundException) {
    }

    return cloneAssessmentVersion
  }

  fun find(criteria: AssessmentVersionCriteria): AssessmentVersion {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
      ?: throw AssessmentVersionNotFoundException("No assessment version found that matches criteria: $criteria")
  }

  fun updateAnswers(assessmentUuid: UUID, request: UpdateAssessmentAnswersRequest) {
    log.info("Adding answers to assessment with UUID $assessmentUuid for tags ${request.tags}")

    assessmentService.findByUuid(assessmentUuid).let {
      request.tags.map { tag ->
        val assessmentVersion = cloneFromPrevious(tag, it)

        assessmentVersion.answers = assessmentVersion.answers.plus(request.answersToAdd)
          .filterNot { thisAnswer -> request.answersToRemove.contains(thisAnswer.key) }

        assessmentVersion.oasys_equivalent = dataMappingService.getOasysEquivalent(assessmentVersion)

        assessmentVersionRepository.save(assessmentVersion)

        log.info("Saved answers to assessment version UUID ${assessmentVersion.uuid}")
      }
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
