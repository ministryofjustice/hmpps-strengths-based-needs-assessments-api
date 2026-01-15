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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID
import kotlin.test.assertEquals

@DisplayName("AssessmentController: /assessment/{assessmentUuid}/delete")
class DeleteTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private fun endpoint(assessmentUuid: UUID? = null) = "/assessment/${assessmentUuid ?: assessment.uuid}/delete"

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.assessmentVersions.add(
      AssessmentVersion(assessment = assessment, versionNumber = 0),
    )
    assessmentRepository.save(assessment)
    clearAllMocks()
    every { telemetryService.assessmentDeleted(any(), any()) } just Runs
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
    verify(exactly = 0) { telemetryService.assessmentDeleted(any(), any()) }
  }

  @Test
  fun `it returns Bad Request when User Name is over the limit`() {
    val request = """
        {
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
    verify(exactly = 0) { telemetryService.assessmentDeleted(any(), any()) }
  }

  @Test
  fun `it returns Not Found when the assessment does not exist`() {
    val request = """
        {
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint(UUID.randomUUID()))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound

    verify(exactly = 0) { telemetryService.assessmentDeleted(any(), any()) }
  }

  @Test
  fun `it deletes the assessment`() {
    val request = """
        {
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    assertThat(assessmentRepository.findByUuid(assessment.uuid)).isNull()
    assertThat(assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid)).isEqualTo(0)

    verify(exactly = 1) {
      telemetryService.assessmentDeleted(
        withArg { assertEquals(assessment.uuid, it.uuid) },
        "user-id",
      )
    }
  }
}
