package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.LocalDateTime

typealias Properties = MutableMap<String, String>

enum class EventProp {
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

@Service
class TelemetryService(
  val client: TelemetryClient,
) {
  private fun track(eventName: String, properties: Properties) = client.trackEvent(eventName, properties, null)

  fun assessmentCreated(assessmentVersion: AssessmentVersion, userId: String, clonedFromVersion: Int? = null) =
    track(
      "ASSESSMENT_CREATED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(EventProp.CLONED_FROM_VERSION.name, clonedFromVersion.toString()) },
    )

  fun assessmentCompleted(assessmentVersion: AssessmentVersion, userId: String) =
    track(
      "ASSESSMENT_COMPLETED",
      please().apply { putAll(propertiesFrom(assessmentVersion, userId)) },
    )

  fun assessmentAnswersUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag) =
    track(
      "ASSESSMENT_ANSWERS_UPDATED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(EventProp.PREVIOUS_STATUS.name, previousStatus.toString()) },
    )

  fun assessmentStatusUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag) =
    track(
      "ASSESSMENT_STATUS_UPDATED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(EventProp.PREVIOUS_STATUS.name, previousStatus.toString()) },
    )

  fun assessmentSoftDeleted(assessment: Assessment, userId: String, versions: List<AssessmentVersion>) =
    track(
      "ASSESSMENT_SOFT_DELETED",
      please().apply { putAll(propertiesFrom(assessment, userId, versions)) },
    )

  fun assessmentUndeleted(assessment: Assessment, userId: String, versions: List<AssessmentVersion>) =
    track(
      "ASSESSMENT_UNDELETED",
      please().apply { putAll(propertiesFrom(assessment, userId, versions)) },
    )

  fun sectionCompleted(assessmentVersion: AssessmentVersion, userId: String, sectionCode: String) =
    track(
      "SECTION_COMPLETED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply { set(EventProp.SECTION_CODE.name, sectionCode) },
    )

  fun sectionUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag, sectionCode: String) =
    track(
      "SECTION_UPDATED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply {
          set(EventProp.PREVIOUS_STATUS.name, previousStatus.toString())
          set(EventProp.SECTION_CODE.name, sectionCode)
        },
    )

  fun questionUpdated(assessmentVersion: AssessmentVersion, userId: String, previousStatus: Tag, sectionCode: String, questionCode: String, removed: Boolean) =
    track(
      "QUESTION_UPDATED",
      please()
        .apply { putAll(propertiesFrom(assessmentVersion, userId)) }
        .apply {
          set(EventProp.PREVIOUS_STATUS.name, previousStatus.toString())
          set(EventProp.SECTION_CODE.name, sectionCode)
          set(EventProp.QUESTION_CODE.name, questionCode)
          set(EventProp.REMOVED.name, removed.toString())
        },
    )

  companion object {
    fun please(): Properties = mutableMapOf()

    fun propertiesFrom(assessmentVersion: AssessmentVersion, userId: String): Properties = mutableMapOf(
      EventProp.USER_ID.name to userId,
      EventProp.TIMESTAMP.name to LocalDateTime.now().toString(),
      EventProp.ASSESSMENT_ID.name to assessmentVersion.assessment.uuid.toString(),
      EventProp.VERSION_NUMBER.name to assessmentVersion.versionNumber.toString(),
      EventProp.STATUS.name to assessmentVersion.tag.toString(),
      EventProp.FORM_VERSION.name to (assessmentVersion.assessment.info?.formVersion ?: "Unknown"),
    )

    fun propertiesFrom(assessment: Assessment, userId: String, versions: List<AssessmentVersion>): Properties = mutableMapOf(
      EventProp.USER_ID.name to userId,
      EventProp.TIMESTAMP.name to LocalDateTime.now().toString(),
      EventProp.ASSESSMENT_ID.name to assessment.uuid.toString(),
      EventProp.FORM_VERSION.name to (assessment.info?.formVersion ?: "Unknown"),
      EventProp.VERSION_FROM.name to versions.minOf { it.versionNumber }.toString(),
      EventProp.VERSION_TO.name to versions.maxOf { it.versionNumber }.toString(),
    )
  }
}
