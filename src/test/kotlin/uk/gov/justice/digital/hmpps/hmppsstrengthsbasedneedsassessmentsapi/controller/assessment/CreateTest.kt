package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.Constraints
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.TelemetryService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import kotlin.test.assertEquals

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: POST /assessment")
class CreateTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val telemetryService: TelemetryService,
) : IntegrationTest() {
  private val endpoint = "/assessment"
  private lateinit var assessment: Assessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.assessmentVersions = listOf(AssessmentVersion(assessment = assessment))
    assessmentRepository.save(assessment)
    clearAllMocks()
    every { telemetryService.assessmentCreated(any(), any(), any()) } returns Unit
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    webTestClient.post().uri(endpoint)
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

    webTestClient.post().uri(endpoint)
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
            "unknownParam": "test",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
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

    val response = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("Validation failure: [userDetails.id - size must be between 0 and ${Constraints.OASYS_USER_ID_MAX_LENGTH}]")
    verify(exactly = 0) { telemetryService.assessmentCreated(any(), any(), any()) }
  }

  @Test
  fun `it returns Bad Request when User Name is over the limit`() {
    val request = """
        {
          "userDetails": { "id": "12345", "name": "${"A".repeat(Constraints.OASYS_USER_NAME_MAX_LENGTH + 1)}" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("Validation failure: [userDetails.name - size must be between 0 and ${Constraints.OASYS_USER_NAME_MAX_LENGTH}]")
    verify(exactly = 0) { telemetryService.assessmentCreated(any(), any(), any()) }
  }

  @Test
  fun `it creates an assessment`() {
    val request = """
          {
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    val response: AssessmentResponse? = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isCreated
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    val newAssessment = assessmentRepository.findByUuid(response?.metaData?.uuid!!)

    assertThat(newAssessment).isNotNull
    assertThat(newAssessment?.assessmentVersions?.count()).isEqualTo(1)
    assertThat(newAssessment?.assessmentVersions?.first()?.assessmentVersionAudit?.count()).isEqualTo(1)

    val audit = newAssessment?.assessmentVersions?.first()?.assessmentVersionAudit?.first()
    assertThat(audit).isNotNull
    assertThat(audit!!.statusFrom).isNull()
    assertThat(audit.statusTo).isNull()
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    verify(exactly = 1) {
      telemetryService.assessmentCreated(
        withArg { assertEquals(newAssessment.assessmentVersions.last().uuid, it.uuid) },
        "user-id",
        null,
      )
    }
  }
}
