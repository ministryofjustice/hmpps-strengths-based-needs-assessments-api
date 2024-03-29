package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

enum class UserAccess(val value: String) {
  READ_ONLY("READ_ONLY"),
  READ_WRITE("READ_WRITE"),
}

enum class LinkStatus {
  USED,
  UNUSED,
}

@Entity
@Table(name = "oasys_sessions")
class Session(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "user_id")
  val userSessionId: String = "",

  @Column(name = "user_access")
  @Enumerated(EnumType.STRING)
  val userAccess: UserAccess = UserAccess.READ_ONLY,

  @Column(name = "user_display_name")
  val userDisplayName: String = "",

  @Column(name = "link_status")
  @Enumerated(EnumType.STRING)
  var linkStatus: LinkStatus = LinkStatus.UNUSED,

  @Column(name = "link_uuid")
  val linkUuid: UUID = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid")
  val oasysAssessment: OasysAssessment = OasysAssessment(),
)
