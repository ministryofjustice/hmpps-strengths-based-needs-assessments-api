package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: POST /assessment")
class CreateTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private val endpoint = "/assessment"
  private lateinit var assessment: Assessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.assessmentVersions = listOf(AssessmentVersion(assessment = assessment))
    assessmentRepository.save(assessment)
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
  }
}
