package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class AssessmentVersionService(
  val assessmentService: AssessmentService,
  val assessmentVersionRepository: AssessmentVersionRepository,
  val dataMappingService: DataMappingService,
) {
  private fun versionCreatedToday(assessmentVersion: AssessmentVersion): Boolean {
    val versionCreatedDate = assessmentVersion.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    return versionCreatedDate == today
  }
  fun clonePreviousOrCreateNew(tag: Tag, assessment: Assessment): AssessmentVersion? {
    return find(AssessmentVersionCriteria(assessment.uuid, tag))?.let {
      if (tag === Tag.UNVALIDATED || versionCreatedToday(it)) {
        return it
      }

      AssessmentVersion(
        tag = tag,
        assessment = assessment,
        answers = it.answers,
        versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
      )
    } ?: run {
      AssessmentVersion(
        tag = tag,
        assessment = assessment,
        versionNumber = assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid),
      )
    }
  }

  fun find(criteria: AssessmentVersionCriteria): AssessmentVersion? {
    val limit = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
    return assessmentVersionRepository.findAll(criteria.getSpecification(), limit).firstOrNull()
  }

  fun updateAnswers(assessmentUuid: UUID, request: UpdateAssessmentAnswersRequest) {
    if (request.tags.contains(Tag.LOCKED)) {
      throw ConflictException("Locked versions cannot be updated")
    }

    log.info("Adding answers to assessment with UUID $assessmentUuid for tags ${request.tags}")

    assessmentService.findByUuid(assessmentUuid).let {
      request.tags.map { tag ->
        clonePreviousOrCreateNew(tag, it)?.let {
          it.answers = it.answers.plus(request.answersToAdd)
            .filterNot { thisAnswer -> request.answersToRemove.contains(thisAnswer.key) }

          it.oasys_equivalent = dataMappingService.getOasysEquivalent(it)

          assessmentVersionRepository.save(it)

          log.info("Saved answers to assessment version UUID ${it.uuid}")
        } ?: throw AssessmentVersionNotFoundException(AssessmentVersionCriteria(assessmentUuid, tag))
      }
    }
  }

  fun cloneAndTag(assessmentVersion: AssessmentVersion, tag: Tag): AssessmentVersion {
    return assessmentVersionRepository.save(
      AssessmentVersion(
        tag = tag,
        assessment = assessmentVersion.assessment,
        answers = assessmentVersion.answers,
      ),
    ).also {
      log.info("Assessment version ${it.uuid} was cloned from ${assessmentVersion.uuid} and tagged ${tag.name}.")
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
