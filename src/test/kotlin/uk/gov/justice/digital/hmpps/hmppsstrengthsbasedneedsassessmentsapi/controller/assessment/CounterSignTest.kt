package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/counter-sign")
class CounterSignTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private val endpoint = { "/assessment/${assessment.uuid}/counter-sign" }

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessmentRepository.save(assessment)
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
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "COUNTERSIGNED"
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
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "COUNTERSIGNED",
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
  fun `it updates the assessment version and returns it as counter-signed`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        tag = Tag.AWAITING_COUNTERSIGN,
        versionNumber = 1,
      ),
    )
    assessmentRepository.save(assessment)

    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "COUNTERSIGNED"
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

    assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(1)
    val counterSignedVersion = updatedAssessment.assessmentVersions.first()

    assertThat(counterSignedVersion.tag).isEqualTo(Tag.COUNTERSIGNED)
    assertThat(counterSignedVersion.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = counterSignedVersion.assessmentVersionAudit.first()
    assertThat(audit.statusFrom).isEqualTo(Tag.AWAITING_COUNTERSIGN)
    assertThat(audit.statusTo).isEqualTo(Tag.COUNTERSIGNED)
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(1)
  }

  @Test
  fun `it returns 404 when the assessment version is not found`() {
    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "COUNTERSIGNED"
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.developerMessage).startsWith("No assessment version found that matches criteria")
    assertThat(response?.developerMessage).contains("versionNumber=1")
  }

  @Test
  fun `it returns Conflict when the requested outcome is invalid`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        tag = Tag.AWAITING_COUNTERSIGN,
        versionNumber = 1,
      ),
    )
    assessmentRepository.save(assessment)

    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "SELF_SIGNED"
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

    assertThat(response?.userMessage).isEqualTo("Invalid outcome status SELF_SIGNED.")
  }

  @Test
  fun `it returns Conflict when the assessment version cannot be counter-signed due to its status`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        tag = Tag.UNSIGNED,
        versionNumber = 1,
      ),
    )
    assessmentRepository.save(assessment)

    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" },
          "outcome": "COUNTERSIGNED"
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

    assertThat(response?.userMessage).isEqualTo("Cannot counter-sign this assessment version. Unexpected status UNSIGNED.")
  }
}
