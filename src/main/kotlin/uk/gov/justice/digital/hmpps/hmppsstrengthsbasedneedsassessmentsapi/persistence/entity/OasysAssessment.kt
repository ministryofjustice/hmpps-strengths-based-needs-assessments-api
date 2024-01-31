package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "oasys_assessments")
class OasysAssessment(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "oasys_assessment_pk")
  val oasysAssessmentPk: String = "",

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid")
  val assessment: Assessment = Assessment(),
)
