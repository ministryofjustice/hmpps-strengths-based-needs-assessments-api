package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import java.util.UUID

@Repository
interface AssessmentVersionRepository : JpaRepository<AssessmentVersion, Long>, JpaSpecificationExecutor<AssessmentVersion> {
  fun findByUuid(uuid: UUID): AssessmentVersion
}
