package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentEvent
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface AssessmentEventRepository :
  JpaRepository<AssessmentEvent, Long>,
  JpaSpecificationExecutor<AssessmentEvent> {
  fun findAllByAssessmentUuid(assessmentUuid: UUID): List<AssessmentEvent>

  fun findAllByAssessmentUuidAndCreatedAtBetween(assessmentUuid: UUID, start: LocalDateTime, end: LocalDateTime): List<AssessmentEvent>
}
