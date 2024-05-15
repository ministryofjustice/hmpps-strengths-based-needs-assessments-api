package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentSubject
import java.util.UUID

@Repository
interface AssessmentSubjectRepository :
  JpaRepository<AssessmentSubject, Long>, JpaSpecificationExecutor<AssessmentSubject> {
  fun findByAssessmentUuid(assessmentUuid: UUID): AssessmentSubject?
}
