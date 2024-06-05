package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AuditableOasysRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.OasysUserDetails
import java.time.LocalDateTime
import java.util.UUID

data class UserDetails(
  val id: String = "",
  val name: String = "",
  val type: UserType = UserType.SAN,
) {
  companion object {
    fun from(request: AuditableOasysRequest) =
      with(request) { UserDetails(userDetails.id, userDetails.name, UserType.OASYS) }
  }
}

enum class UserType {
  OASYS,
  SAN,
}

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

  @Type(JsonType::class)
  @Column(name = "user_details")
  var userDetails: UserDetails = UserDetails(),

  @Column(name = "status_from")
  var statusFrom: Tag? = null,

  @Column(name = "status_to")
  var statusTo: Tag? = null,
)
