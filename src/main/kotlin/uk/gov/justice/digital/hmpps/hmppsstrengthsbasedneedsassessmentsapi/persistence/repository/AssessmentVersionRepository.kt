package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import java.util.UUID

@Repository
interface AssessmentVersionRepository : JpaRepository<AssessmentVersion, Long>, JpaSpecificationExecutor<AssessmentVersion> {
  fun findByUuid(uuid: UUID): AssessmentVersion

  @Query("SELECT * FROM assessments_versions WHERE assessment_uuid = :assessmentUuid AND deleted = TRUE", nativeQuery = true)
  fun findAllDeleted(assessmentUuid: UUID): List<AssessmentVersion>

  @Query("SELECT COUNT(1) FROM assessments_versions WHERE assessment_uuid = :assessmentUuid", nativeQuery = true)
  fun countVersionWhereAssessmentUuid(assessmentUuid: UUID): Int
}
