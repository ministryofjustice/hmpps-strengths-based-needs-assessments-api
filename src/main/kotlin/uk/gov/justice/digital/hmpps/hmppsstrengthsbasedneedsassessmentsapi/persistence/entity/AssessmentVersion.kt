package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.*

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
  val type: AnswerType,
  val description: String,
  val options: List<Option>?,
  val value: String?,
  val values: List<String>?,
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
