package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.vladmihalcea.hibernate.type.json.JsonType
import io.swagger.v3.oas.annotations.media.Schema
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

enum class EventType(val value: String) {
  UPDATE_ANSWERS("UPDATE_ANSWERS"),
  UPDATE_FORM_VERSION("UPDATE_FORM_VERSION"),
  OASYS_EVENT("OASYS_EVENT"),
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = UpdateAnswers::class, name = "UPDATE_ANSWERS"),
  JsonSubTypes.Type(value = UpdateFormVersion::class, name = "UPDATE_FORM_VERSION"),
  JsonSubTypes.Type(value = OASysEvent::class, name = "OASYS_EVENT"),
)

sealed class EventData(
  val type: EventType,
)

data class UpdateAnswers(
  val added: Map<String, Answer>,
  val removed: List<String>,
) : EventData(EventType.UPDATE_ANSWERS)

data class UpdateFormVersion(
  val version: String,
) : EventData(EventType.UPDATE_FORM_VERSION)

data class OASysEvent(
  val tag: Tag,
) : EventData(EventType.OASYS_EVENT)

data class User(
  @Schema(description = "User ID", example = "111111")
  val id: String = "",
  @Schema(description = "User name", example = "John Doe")
  val name: String = "",
)

@Entity
@Table(name = "assessment_events")
class AssessmentEvent(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Type(JsonType::class)
  @Column(name = "user_details", nullable = false)
  var user: User,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "uuid", updatable = false, nullable = false)
  val assessment: Assessment,

  @Type(JsonType::class)
  @Column(name="event_data", nullable = false)
  val eventData: EventData,
) {
  companion object {
    fun from(assessment: Assessment, user: User, eventData: EventData) =
      AssessmentEvent(assessment = assessment, user = user, eventData = eventData)
  }
}
