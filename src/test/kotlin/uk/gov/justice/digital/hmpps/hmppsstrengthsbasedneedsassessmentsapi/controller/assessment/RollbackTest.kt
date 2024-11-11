package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/rollback")
class RollbackTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private fun endpoint(assessmentUuid: UUID? = null) =
    "/assessment/${assessmentUuid ?: assessment.uuid}/rollback"

  private lateinit var assessment: Assessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessmentRepository.save(assessment)
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .bodyValue(request)
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_OASYS' is not present on the JWT`() {
    val request = """
        {
          "versionNumber": 1,
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
          "versionNumber": 1,
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
  fun `it returns Bad Request when the version number is negative`() {
    val request = """
        {
          "versionNumber": -1,
          "userDetails": { "id": "user-id", "name": "John Doe" }
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

    assertThat(response?.userMessage).isEqualTo("Validation failure: [versionNumber - must be greater than or equal to 0]")
  }

  @Test
  fun `it returns Bad Request when User ID is over the limit`() {
    val request = """
        {
          "versionNumber": 0,
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
  }

  @Test
  fun `it returns Bad Request when User Name is over the limit`() {
    val request = """
        {
          "versionNumber": 0,
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
  }

  @Test
  fun `it returns Not Found when the assessment version does not exist`() {
    val request = """
        {
          "versionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint(UUID.fromString("00000000-0000-0000-0000-000000000000")))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.developerMessage).startsWith("No assessment version found that matches criteria")
    assertThat(response?.developerMessage).contains("assessmentUuid=00000000-0000-0000-0000-000000000000")
  }

  @Test
  fun `it rolls back an assessment for a given version number`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(versionNumber = 1, assessment = assessment, tag = Tag.AWAITING_COUNTERSIGN),
    )

    assessmentRepository.save(assessment)

    val request = """
        {
          "versionNumber": 1,
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

    assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(1)
    val rolledBackVersion = updatedAssessment.assessmentVersions.first()

    assertThat(rolledBackVersion.tag).isEqualTo(Tag.ROLLED_BACK)
    assertThat(rolledBackVersion.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = rolledBackVersion.assessmentVersionAudit.first()
    assertThat(audit.statusFrom).isEqualTo(Tag.AWAITING_COUNTERSIGN)
    assertThat(audit.statusTo).isEqualTo(Tag.ROLLED_BACK)
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(1)
  }

  @Test
  fun `it returns Conflict when the assessment for the given version number is not in the correct state`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(versionNumber = 1, assessment = assessment, tag = Tag.UNSIGNED),
    )

    assessmentRepository.save(assessment)

    val request = """
        {
          "versionNumber": 1,
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

    assertThat(response?.userMessage).isEqualTo("Cannot rollback this assessment version. Unexpected status UNSIGNED.")
  }
}
