package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.Message
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID
import kotlin.test.assertTrue

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/undelete")
class UnDeleteTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private fun endpoint(assessmentUuid: UUID? = null) =
    "/assessment/${assessmentUuid ?: assessment.uuid}/undelete"

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.assessmentVersions = listOf(
      AssessmentVersion(assessment = assessment, versionNumber = 0),
      AssessmentVersion(assessment = assessment, versionNumber = 1, deleted = true),
      AssessmentVersion(assessment = assessment, versionNumber = 2, deleted = true),
      AssessmentVersion(assessment = assessment, versionNumber = 3),
    )
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
          "versionFrom": 0,
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
          "versionFrom": 0,
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
  fun `it returns Not Found when the assessment does not exist`() {
    val request = """
        {
          "versionFrom": 0,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    webTestClient.post().uri(endpoint(UUID.randomUUID()))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound

    assertThat(assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().size).isEqualTo(2)
    assertThat(assessmentVersionRepository.findAllDeleted(assessment.uuid).size).isEqualTo(2)
  }

  @Test
  fun `it returns Conflict when the assessment version has not been soft-deleted`() {
    val request = """
        {
          "versionFrom": 3,
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

    assertThat(response?.userMessage).isEqualTo("No assessment versions found for un-deletion")

    assertThat(assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().size).isEqualTo(2)
    assertThat(assessmentVersionRepository.findAllDeleted(assessment.uuid).size).isEqualTo(2)
  }

  @Test
  fun `it undeletes the latest assessment versions`() {
    val request = """
        {
          "versionFrom": 2,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(Message::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.message).isEqualTo("Successfully un-deleted 1 assessment versions")

    assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().run {
      assertThat(count()).isEqualTo(3)
      assertTrue(all { version -> with(version) { !deleted && versionNumber in listOf(0, 2, 3) } })
    }

    assessmentVersionRepository.findAllDeleted(assessment.uuid).run {
      assertThat(count()).isEqualTo(1)
      assertTrue(all { version -> with(version) { deleted && versionNumber in listOf(1) } })
    }
  }

  @Test
  fun `it undeletes a specific range of assessment versions`() {
    val request = """
        {
          "versionFrom": 1,
          "versionTo": 2,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(Message::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.message).isEqualTo("Successfully un-deleted 1 assessment versions")

    assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().run {
      assertThat(count()).isEqualTo(3)
      assertTrue(all { version -> with(version) { !deleted && versionNumber in listOf(0, 1, 3) } })
    }

    assessmentVersionRepository.findAllDeleted(assessment.uuid).run {
      assertThat(count()).isEqualTo(1)
      assertTrue(all { version -> with(version) { deleted && versionNumber in listOf(2) } })
    }
  }
}
