package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.CascadeType
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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

typealias OasysEquivalent = Map<String, Any?>

enum class Tag {
  UNSIGNED,
  LOCKED_INCOMPLETE,
  SELF_SIGNED,
  AWAITING_COUNTERSIGN,
  AWAITING_DOUBLE_COUNTERSIGN,
  COUNTERSIGNED,
  DOUBLE_COUNTERSIGNED,
  REJECTED,
  ROLLED_BACK,
  ;

  fun isNotLocked(): Boolean {
    return this == UNSIGNED
  }

  companion object {
    fun lockedTags(): Set<Tag> {
      return entries.toTypedArray().subtract(setOf(UNSIGNED))
    }

    fun tagsThatCanRollback(): Set<Tag> {
      return setOf(
        AWAITING_COUNTERSIGN,
        AWAITING_DOUBLE_COUNTERSIGN,
        COUNTERSIGNED,
        DOUBLE_COUNTERSIGNED,
        LOCKED_INCOMPLETE,
        REJECTED,
        SELF_SIGNED,
      )
    }
  }
}

enum class SignType {
  SELF,
  COUNTERSIGN,
  ;

  fun into(): Tag {
    return when (this) {
      SELF -> Tag.SELF_SIGNED
      COUNTERSIGN -> Tag.AWAITING_COUNTERSIGN
    }
  }
}

@Entity
@Table(name = "assessments_versions")
data class AssessmentVersion(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "updated_at")
  var updatedAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "tag")
  @Enumerated(EnumType.STRING)
  var tag: Tag = Tag.UNSIGNED,

  @Type(JsonType::class)
  @Column(name = "answers")
  var answers: Answers = mutableMapOf(),

  @Type(JsonType::class)
  @Column(name = "oasys_equivalent")
  var oasysEquivalents: OasysEquivalent = mutableMapOf(),

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", updatable = false, nullable = false)
  val assessment: Assessment = Assessment(),

  @Column(name = "version_number")
  val versionNumber: Int = 0,

  @OneToMany(mappedBy = "assessmentVersion", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
  var assessmentVersionAudit: List<AssessmentVersionAudit> = listOf(),
) {
  fun isUpdatable(): Boolean {
    val versionUpdatedDate = updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    return versionUpdatedDate == today && tag.isNotLocked()
  }

  fun setAnswers(answersToAdd: Answers, answersToRemove: List<String>) {
    answers = answers.plus(answersToAdd)
      .filterNot { answersToRemove.contains(it.key) }
    updatedAt = LocalDateTime.now()
  }
}
