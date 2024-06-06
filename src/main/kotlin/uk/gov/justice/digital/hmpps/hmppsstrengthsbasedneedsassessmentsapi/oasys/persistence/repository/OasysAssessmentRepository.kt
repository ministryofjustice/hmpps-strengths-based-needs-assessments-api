package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import java.util.UUID

@Repository
interface OasysAssessmentRepository : JpaRepository<OasysAssessment, Long> {
  fun findByUuid(uuid: UUID): OasysAssessment?
  fun findByOasysAssessmentPk(oasysAssessmentPk: String): OasysAssessment?

  @Query("SELECT * FROM oasys_assessments WHERE oasys_assessment_pk = :oasysAssessmentPk AND deleted = TRUE", nativeQuery = true)
  fun findDeletedByOasysAssessmentPk(oasysAssessmentPk: String): OasysAssessment?
}
