package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.LinkStatus
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session
import java.util.UUID

// Might be worth changing this to a Redis implementation?
@Repository
interface SessionRepository : JpaRepository<Session, Long> {
  fun findByLinkUuidAndLinkStatus(uuid: UUID, linkStatus: LinkStatus): Session?
}
