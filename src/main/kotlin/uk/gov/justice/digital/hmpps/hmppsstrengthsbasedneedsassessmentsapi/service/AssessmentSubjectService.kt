package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SubjectDetailsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentSubject
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SubjectDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentSubjectRepository

@Service
class AssessmentSubjectService(
  val assessmentSubjectRepository: AssessmentSubjectRepository,
) {
  fun create(assessment: Assessment, subjectDetails: SubjectDetailsRequest): AssessmentSubject {
    return assessmentSubjectRepository.save(
      AssessmentSubject(
        assessment = assessment,
        subjectDetails = SubjectDetails.from(subjectDetails),
      ),
    ).also {
      log.info("Created subject for assessment: ${assessment.uuid}")
    }
  }

  fun updateOrCreate(assessment: Assessment, subjectDetails: SubjectDetailsRequest): AssessmentSubject {
    return find(assessment)?.let {
      it.subjectDetails = SubjectDetails.from(subjectDetails)
      assessmentSubjectRepository.save(it)
        .also { log.info("Updated subject for assessment ${assessment.uuid}") }
    } ?: run {
      create(assessment, subjectDetails)
    }
  }

  fun find(assessment: Assessment): AssessmentSubject? {
    return assessmentSubjectRepository.findByAssessment(assessment)
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
