package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Subject
@Repository
interface SubjectRepository : JpaRepository<Subject, Long> {
  fun findSubjectByCrn(crn: String): Subject?
}
