package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysAssessment
import java.util.UUID

@Repository
interface OasysAssessmentRepository : JpaRepository<OasysAssessment, Long> {
  fun findByUuid(uuid: UUID): OasysAssessment?
  fun findByOasysAssessmentPk(oasysAssessmentId: String): OasysAssessment?
}
