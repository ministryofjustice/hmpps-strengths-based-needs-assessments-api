package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import jakarta.persistence.*
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.UserAccess
import java.time.LocalDateTime
import java.util.*

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
