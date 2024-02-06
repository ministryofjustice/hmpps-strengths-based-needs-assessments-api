package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.UUID

enum class AnswerType {
  TEXT,
  TEXT_AREA,
  RADIO,
  CHECKBOX,
  DROPDOWN,
  COLLECTION,
  DATE,
}

class Option(
  val value: String,
  val text: String,
)

class Answer(
  val type: AnswerType = AnswerType.TEXT,
  val description: String = "",
  val options: List<Option>? = null,
  val value: String? = null,
  val values: List<String>? = null,
)

typealias Answers = Map<String, Answer>

typealias OasysEquivalent = Map<String, Any>

@Entity
@Table(name = "assessments_versions")
class AssessmentVersion(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "tag")
  val tag: String = "",

  @Type(JsonType::class)
  @Column(name = "answers")
  var answers: Answers = mutableMapOf(),

  @Type(JsonType::class)
  @Column(name = "oasys_equivalent")
  var oasys_equivalent: OasysEquivalent = mutableMapOf(),

  @OneToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", unique = true, updatable = false, nullable = false)
  val assessment: Assessment? = null,
)
