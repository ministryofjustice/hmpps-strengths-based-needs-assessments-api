package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.LocalDateTime

enum class Event {
  ASSESSMENT_CREATED,
  ASSESSMENT_COMPLETED,
  ASSESSMENT_ANSWERS_UPDATED,
  ASSESSMENT_STATUS_UPDATED,
  ASSESSMENT_SOFT_DELETED,
  ASSESSMENT_UNDELETED,
  SECTION_COMPLETED,
  SECTION_UPDATED,
  QUESTION_UPDATED,
}

enum class Property {
  ASSESSMENT_ID,
  CLONED_FROM_VERSION,
  FORM_VERSION,
  PREVIOUS_STATUS,
  QUESTION_CODE,
  REMOVED,
  SECTION_CODE,
  STATUS,
  TIMESTAMP,
  USER_ID,
  VERSION_FROM,
  VERSION_NUMBER,
  VERSION_TO,
}

typealias Properties = MutableMap<Property, String>

@Service
class TelemetryService(
  val client: TelemetryClient,
) {
  private fun track(event: Event, properties: Properties) =
    client.trackEvent(event.name, properties.mapKeys { it.key.name }, null)

  fun assessmentCreated(assessmentVersion: AssessmentVersion, userId: String, clonedFromVersion: Int? = null) =
    track(
      Event.ASSESSMENT_CREATED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(Property.CLONED_FROM_VERSION, clonedFromVersion.toString()) },
    )

  fun assessmentCompleted(assessmentVersion: AssessmentVersion, userId: String) =
    track(
      Event.ASSESSMENT_COMPLETED,
      please().apply { putAll(propertiesFrom(assessmentVersion, userId)) },
    )

  fun assessmentAnswersUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag) =
    track(
      Event.ASSESSMENT_ANSWERS_UPDATED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(Property.PREVIOUS_STATUS, previousStatus.toString()) },
    )

  fun assessmentStatusUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag) =
    track(
      Event.ASSESSMENT_STATUS_UPDATED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(Property.PREVIOUS_STATUS, previousStatus.toString()) },
    )

  fun assessmentSoftDeleted(assessment: Assessment, userId: String, versions: List<AssessmentVersion>) =
    track(
      Event.ASSESSMENT_SOFT_DELETED,
      please().apply { putAll(propertiesFrom(assessment, userId, versions)) },
    )

  fun assessmentUndeleted(assessment: Assessment, userId: String, versions: List<AssessmentVersion>) =
    track(
      Event.ASSESSMENT_UNDELETED,
      please().apply { putAll(propertiesFrom(assessment, userId, versions)) },
    )

  fun sectionCompleted(assessmentVersion: AssessmentVersion, userId: String, sectionCode: String) =
    track(
      Event.SECTION_COMPLETED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(Property.SECTION_CODE, sectionCode) },
    )

  fun sectionUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag, sectionCode: String) =
    track(
      Event.SECTION_UPDATED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply {
          set(Property.PREVIOUS_STATUS, previousStatus.toString())
          set(Property.SECTION_CODE, sectionCode)
        },
    )

  fun questionUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag, sectionCode: String, questionCode: String, removed: Boolean) =
    track(
      Event.QUESTION_UPDATED,
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply {
          set(Property.PREVIOUS_STATUS, previousStatus.toString())
          set(Property.SECTION_CODE, sectionCode)
          set(Property.QUESTION_CODE, questionCode)
          set(Property.REMOVED, removed.toString())
        },
    )

  companion object {
    fun please(): Properties = mutableMapOf()

    fun propertiesFrom(assessmentVersion: AssessmentVersion, userId: String): Properties = mutableMapOf(
      Property.USER_ID to userId,
      Property.TIMESTAMP to LocalDateTime.now().toString(),
      Property.ASSESSMENT_ID to assessmentVersion.assessment.uuid.toString(),
      Property.VERSION_NUMBER to assessmentVersion.versionNumber.toString(),
      Property.STATUS to assessmentVersion.tag.toString(),
      Property.FORM_VERSION to (assessmentVersion.assessment.info?.formVersion ?: "Unknown"),
    )

    fun propertiesFrom(assessment: Assessment, userId: String, versions: List<AssessmentVersion>): Properties = mutableMapOf(
      Property.USER_ID to userId,
      Property.TIMESTAMP to LocalDateTime.now().toString(),
      Property.ASSESSMENT_ID to assessment.uuid.toString(),
      Property.FORM_VERSION to (assessment.info?.formVersion ?: "Unknown"),
      Property.VERSION_FROM to versions.minOf { it.versionNumber }.toString(),
      Property.VERSION_TO to versions.maxOf { it.versionNumber }.toString(),
    )
  }
}
