package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import com.microsoft.applicationinsights.TelemetryClient
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
@DisplayName("TelemetryService")
class TelemetryServiceTest {
  private val telemetryClient: TelemetryClient = mockk()
  private val telemetryService = TelemetryService(telemetryClient)
  private val assessment = Assessment(
    info = AssessmentFormInfo(
      formVersion = "1.0",
    ),
  )
  private val assessmentVersion = AssessmentVersion(
    versionNumber = 13,
    assessment = assessment,
  )

  @BeforeEach
  fun setUp() {
    assessment.assessmentVersions = mutableListOf(assessmentVersion)
    clearAllMocks()
    every { telemetryClient.trackEvent(any(), any(), null) } just Runs
  }

  fun verifyTracked(event: Event, properties: Map<String, String>) = verify(exactly = 1) {
    telemetryClient.trackEvent(event.name, withArg { assertPropertiesMatch(properties, it) }, null)
  }

  fun assertPropertiesMatch(expected: Map<String, String>, actual: Map<String, String>) {
    assertEquals(expected.keys, actual.keys)
    actual.keys.forEach {
      when (it) {
        Property.TIMESTAMP.name -> assertTrue(Duration.between(LocalDateTime.parse(expected[it]!!), LocalDateTime.parse(actual[it]!!)).seconds.absoluteValue <= 2)
        else -> assertEquals(expected[it], actual[it])
      }
    }
  }

  @Test
  fun assessmentCreated() {
    telemetryService.assessmentCreated(assessmentVersion, "user-id", 12)

    verifyTracked(
      Event.ASSESSMENT_CREATED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.CLONED_FROM_VERSION.name to "12",
      ),
    )
  }

  @Test
  fun assessmentCompleted() {
    telemetryService.assessmentCompleted(assessmentVersion, "user-id")

    verifyTracked(
      Event.ASSESSMENT_COMPLETED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
      ),
    )
  }

  @Test
  fun assessmentAnswersUpdated() {
    telemetryService.assessmentAnswersUpdated(assessmentVersion, "user-id", Tag.LOCKED_INCOMPLETE)

    verifyTracked(
      Event.ASSESSMENT_ANSWERS_UPDATED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.PREVIOUS_STATUS.name to "LOCKED_INCOMPLETE",
      ),
    )
  }

  @Test
  fun assessmentStatusUpdated() {
    telemetryService.assessmentStatusUpdated(assessmentVersion, "user-id", Tag.LOCKED_INCOMPLETE)

    verifyTracked(
      Event.ASSESSMENT_STATUS_UPDATED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.PREVIOUS_STATUS.name to "LOCKED_INCOMPLETE",
      ),
    )
  }

  @Test
  fun assessmentDeleted() {
    telemetryService.assessmentDeleted(assessment, "user-id")

    verifyTracked(
      Event.ASSESSMENT_DELETED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.FORM_VERSION.name to "1.0",
      ),
    )
  }

  @Test
  fun assessmentSoftDeleted() {
    telemetryService.assessmentSoftDeleted(assessment, "user-id", assessment.assessmentVersions)

    verifyTracked(
      Event.ASSESSMENT_SOFT_DELETED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.FORM_VERSION.name to "1.0",
        Property.VERSION_FROM.name to "13",
        Property.VERSION_TO.name to "13",
      ),
    )
  }

  @Test
  fun assessmentUndeleted() {
    telemetryService.assessmentUndeleted(assessment, "user-id", assessment.assessmentVersions)

    verifyTracked(
      Event.ASSESSMENT_UNDELETED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.FORM_VERSION.name to "1.0",
        Property.VERSION_FROM.name to "13",
        Property.VERSION_TO.name to "13",
      ),
    )
  }

  @Test
  fun sectionCompleted() {
    telemetryService.sectionCompleted(assessmentVersion, "user-id", "section-id")

    verifyTracked(
      Event.SECTION_COMPLETED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.SECTION_CODE.name to "section-id",
      ),
    )
  }

  @Test
  fun sectionUpdated() {
    telemetryService.sectionUpdated(assessmentVersion, "user-id", Tag.LOCKED_INCOMPLETE, "section-id")

    verifyTracked(
      Event.SECTION_UPDATED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.PREVIOUS_STATUS.name to "LOCKED_INCOMPLETE",
        Property.SECTION_CODE.name to "section-id",
      ),
    )
  }

  @Test
  fun questionUpdated() {
    telemetryService.questionUpdated(assessmentVersion, "user-id", Tag.LOCKED_INCOMPLETE, "section-id", "question-id", false)

    verifyTracked(
      Event.QUESTION_UPDATED,
      mapOf(
        Property.USER_ID.name to "user-id",
        Property.TIMESTAMP.name to LocalDateTime.now().toString(),
        Property.ASSESSMENT_ID.name to assessment.uuid.toString(),
        Property.ASSESSMENT_VERSION.name to "13",
        Property.STATUS.name to "UNSIGNED",
        Property.FORM_VERSION.name to "1.0",
        Property.PREVIOUS_STATUS.name to "LOCKED_INCOMPLETE",
        Property.SECTION_CODE.name to "section-id",
        Property.QUESTION_CODE.name to "question-id",
        Property.REMOVED.name to "false",
      ),
    )
  }
}
