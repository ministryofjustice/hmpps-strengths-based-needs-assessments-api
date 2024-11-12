package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions
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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/lock")
class LockTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private val endpoint = { "/assessment/${assessment.uuid}/lock" }

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
  }

  @Test
  fun `it updates and returns the version of the assessment as Locked`() {
    val latestVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(1),
      versionNumber = 1,
      answers = mapOf("q1" to Answer(value = "val1")),
    )

    val previousVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(2),
      versionNumber = 0,
    )

    assessment.assessmentVersions = listOf(latestVersion, previousVersion)
    assessmentRepository.save(assessment)

    val request = """
        {
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

    Assertions.assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)

    val updatedLatestVersion = updatedAssessment.assessmentVersions.find { it.uuid == latestVersion.uuid }
    val updatedPreviousVersion = updatedAssessment.assessmentVersions.find { it.uuid == previousVersion.uuid }

    Assertions.assertThat(updatedLatestVersion).isNotNull
    Assertions.assertThat(updatedLatestVersion?.tag).isEqualTo(Tag.LOCKED_INCOMPLETE)
    Assertions.assertThat(updatedPreviousVersion).isNotNull
    Assertions.assertThat(updatedPreviousVersion?.tag).isEqualTo(Tag.UNSIGNED)

    Assertions.assertThat(updatedLatestVersion!!.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = updatedLatestVersion.assessmentVersionAudit.first()
    Assertions.assertThat(audit.statusFrom).isEqualTo(Tag.UNSIGNED)
    Assertions.assertThat(audit.statusTo).isEqualTo(Tag.LOCKED_INCOMPLETE)
    Assertions.assertThat(audit.userDetails.id).isEqualTo("user-id")
    Assertions.assertThat(audit.userDetails.name).isEqualTo("John Doe")

    Assertions.assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    Assertions.assertThat(response?.metaData?.versionNumber).isEqualTo(updatedLatestVersion.versionNumber).isEqualTo(1)
  }

  @Test
  fun `it returns Conflict when the assessment is already locked`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        createdAt = LocalDateTime.now().minusHours(1),
        tag = Tag.LOCKED_INCOMPLETE,
        versionNumber = 0,
      ),
    )

    assessmentRepository.save(assessment)

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
      .expectStatus().isEqualTo(409)
  }
}
