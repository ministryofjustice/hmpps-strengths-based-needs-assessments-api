package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessment_subject")
data class AssessmentSubject(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Type(JsonType::class)
  @Column(name = "subject_details")
  var subjectDetails: SubjectDetails = SubjectDetails(),

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", updatable = false, nullable = false)
  val assessment: Assessment = Assessment(),
)
