package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import kotlin.test.assertEquals

@DisplayName("AssessmentController: /assessment/{assessmentUuid}/sign")
class SignTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private val endpoint = { "/assessment/${assessment.uuid}/sign" }

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessmentRepository.save(assessment)
    clearAllMocks()
    every { telemetryService.assessmentStatusUpdated(any(), any(), any()) } just Runs
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_OASYS' is not present on the JWT`() {
    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation())
      .bodyValue(request)
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun `it returns Bad Request when the request body contains unknown parameters`() {
    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "foo": "bar"
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
  }

  @Test
  fun `it returns Bad Request when User ID is over the limit`() {
    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "${"1".repeat(Constraints.OASYS_USER_ID_MAX_LENGTH + 1)}", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("Validation failure: [userDetails.id - size must be between 0 and ${Constraints.OASYS_USER_ID_MAX_LENGTH}]")
    verify(exactly = 0) { telemetryService.assessmentStatusUpdated(any(), any(), any()) }
  }

  @Test
  fun `it returns Bad Request when User Name is over the limit`() {
    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "12345", "name": "${"A".repeat(Constraints.OASYS_USER_NAME_MAX_LENGTH + 1)}" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("Validation failure: [userDetails.name - size must be between 0 and ${Constraints.OASYS_USER_NAME_MAX_LENGTH}]")
    verify(exactly = 0) { telemetryService.assessmentStatusUpdated(any(), any(), any()) }
  }

  @Test
  fun `it updates the latest version of the assessment as Signed and returns it`() {
    val latestVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(1),
      versionNumber = 1,
      answers = mapOf("assessment_complete" to Answer(value = "YES")),
    )
    val previousVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(2),
      versionNumber = 0,
    )

    assessment.assessmentVersions = mutableListOf(latestVersion, previousVersion)
    assessmentRepository.save(assessment)

    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response: AssessmentResponse? = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

    assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)
    val updatedLatestVersion = updatedAssessment.assessmentVersions.find { it.uuid == latestVersion.uuid }
    val updatedPreviousVersion = updatedAssessment.assessmentVersions.find { it.uuid == previousVersion.uuid }

    assertThat(updatedLatestVersion).isNotNull
    assertThat(updatedLatestVersion?.tag).isEqualTo(Tag.SELF_SIGNED)
    assertThat(updatedPreviousVersion).isNotNull
    assertThat(updatedPreviousVersion?.tag).isEqualTo(Tag.UNSIGNED)

    assertThat(updatedLatestVersion!!.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = updatedLatestVersion.assessmentVersionAudit.first()
    assertThat(audit.statusFrom).isEqualTo(Tag.UNSIGNED)
    assertThat(audit.statusTo).isEqualTo(Tag.SELF_SIGNED)
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(updatedLatestVersion.versionNumber).isEqualTo(1)

    verify(exactly = 1) {
      telemetryService.assessmentStatusUpdated(
        withArg { assertEquals(updatedLatestVersion.uuid, it.uuid) },
        "user-id",
        Tag.UNSIGNED,
      )
    }
  }

  @Test
  fun `it returns Conflict when the assessment is already signed`() {
    assessment.assessmentVersions = mutableListOf(
      AssessmentVersion(
        assessment = assessment,
        answers = mapOf("assessment_complete" to Answer(value = "YES")),
        tag = Tag.SELF_SIGNED,
        versionNumber = 2,
      ),
    )
    assessmentRepository.save(assessment)

    val request = """
        {
          "signType": "SELF",
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isEqualTo(409)
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("The current assessment version is already SELF_SIGNED.")

    verify(exactly = 0) { telemetryService.assessmentStatusUpdated(any(), any(), any()) }
  }
}
