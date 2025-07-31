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
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessment_aggregates")
class AssessmentAggregate(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "updated_at")
  var updatedAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "events_from")
  val from: LocalDateTime = LocalDateTime.now(),

  @Column(name = "events_to")
  val to: LocalDateTime = LocalDateTime.now(),

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", updatable = false, nullable = false)
  val assessment: Assessment,

  @Type(JsonType::class)
  private val answers: MutableMap<String, Answer> = mutableMapOf(),
) {
  fun applyEvents(events: List<AssessmentEvent>): AssessmentAggregate {
    events.filter { it.eventData.type == EventType.UPDATE_ANSWERS }
      .sortedBy { it.createdAt }
      .map { it.eventData as UpdateAnswers }
      .forEach { transaction ->
        transaction.added.entries.forEach { this.answers[it.key] = it.value }
        transaction.removed.forEach { this.answers.remove(it) }
      }
    return this
  }

  fun applyEvents(event: AssessmentEvent) = applyEvents(listOf(event))

  fun getAnswers() = this.answers.toMap()

  companion object {
    fun from(previous: AssessmentAggregate) = AssessmentAggregate(
      assessment = previous.assessment,
      from = previous.from,
      to = previous.to,
      answers = previous.answers.toMutableMap(),
    )
  }
}
