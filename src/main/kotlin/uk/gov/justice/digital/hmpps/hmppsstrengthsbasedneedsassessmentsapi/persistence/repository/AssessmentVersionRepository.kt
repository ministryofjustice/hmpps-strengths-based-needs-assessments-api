package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import java.util.UUID

@Repository
interface AssessmentVersionRepository : JpaRepository<AssessmentVersion, Long> {
  fun findByUuid(uuid: UUID): AssessmentVersion?

  fun findByAssessmentUuid(assessmentUUID: UUID): Collection<AssessmentVersion>

  fun findByAssessmentUuidAndTag(assessmentUUID: UUID, tag: String): Collection<AssessmentVersion>
}
