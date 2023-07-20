package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import java.util.UUID

@Repository
interface AssessmentRepository : JpaRepository<Assessment, Long> {
  fun findByUuid(uuid: UUID): Assessment?
  fun findByOasysAssessmentId(oasysAssessmentId: String): Assessment?
}
