package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "assessments_form_info")
class AssessmentFormInfo(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "form_name")
  val formName: String = "",

  @Column(name = "form_version")
  val formVersion: String = "",

  @OneToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", unique = true, updatable = false, nullable = false)
  val assessment: Assessment? = null,
)
