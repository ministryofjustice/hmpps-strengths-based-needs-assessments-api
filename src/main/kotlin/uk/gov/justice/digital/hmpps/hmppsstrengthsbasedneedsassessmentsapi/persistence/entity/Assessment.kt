package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

enum class AnswerType {
  TEXT,
  TEXTAREA,
  RADIO,
  CHECKBOX,
  DROPDOWN,
}

class Option(
  val value: String,
  val description: String,
)

class Answer(
  val type: AnswerType,
  val description: String,
  val options: List<Option>?,
  val value: String?,
)

typealias Answers = Map<String, Answer>

@Entity
@Table(name = "assessments")
class Assessment(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "oasys_assessment_id")
  val oasysAssessmentId: String = "",

  @Type(JsonType::class)
  @Column(name = "answers")
  var answers: Answers = mutableMapOf(),
) : Serializable
