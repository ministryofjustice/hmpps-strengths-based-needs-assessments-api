package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessment_version_audit")
data class AssessmentVersionAudit(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_version_uuid", referencedColumnName = "uuid", updatable = false, nullable = false)
  val assessmentVersion: AssessmentVersion = AssessmentVersion(),

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "user_details")
  var userDetails: UserDetails = UserDetails(),

  @Column(name = "status_from")
  @Enumerated(EnumType.STRING)
  var statusFrom: Tag? = null,

  @Column(name = "status_to")
  @Enumerated(EnumType.STRING)
  var statusTo: Tag? = null,
)
