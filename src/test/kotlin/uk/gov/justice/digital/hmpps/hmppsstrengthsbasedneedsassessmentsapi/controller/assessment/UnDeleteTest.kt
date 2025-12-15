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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("AssessmentController: /assessment/{assessmentUuid}/undelete")
class UnDeleteTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private fun endpoint(assessmentUuid: UUID? = null) = "/assessment/${assessmentUuid ?: assessment.uuid}/undelete"

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.assessmentVersions = mutableListOf(
      AssessmentVersion(assessment = assessment, versionNumber = 0),
      AssessmentVersion(assessment = assessment, versionNumber = 1),
      AssessmentVersion(assessment = assessment, versionNumber = 2, deleted = true),
      AssessmentVersion(assessment = assessment, versionNumber = 3, deleted = true),
    )
    assessmentRepository.save(assessment)
    clearAllMocks()
    every { telemetryService.assessmentUndeleted(any(), any(), any()) } just Runs
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
  fun `it returns Bad Request when the versionFrom is negative`() {
    val request = """
        {
          "versionFrom": -1,
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

    assertThat(response?.userMessage).isEqualTo("Validation failure: [versionFrom - must be greater than or equal to 0]")
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
  }

  @Test
  fun `it returns Bad Request when the versionTo is negative`() {
    val request = """
        {
          "versionFrom": 0,
          "versionTo": -1,
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

    assertThat(response?.userMessage).isEqualTo("Validation failure: [versionTo - must be greater than or equal to 0]")
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
  }

  @Test
  fun `it returns Bad Request when User ID is over the limit`() {
    val request = """
        {
          "versionFrom": 0,
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
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
  }

  @Test
  fun `it returns Bad Request when User Name is over the limit`() {
    val request = """
        {
          "versionFrom": 0,
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
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
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
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
  }

  @Test
  fun `it returns Conflict when the assessment version has not been soft-deleted`() {
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
      .expectStatus().isEqualTo(409)
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("No assessment versions found for un-deletion")

    assertThat(assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().size).isEqualTo(2)
    assertThat(assessmentVersionRepository.findAllDeleted(assessment.uuid).size).isEqualTo(2)
    verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
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
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(3)

    assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().run {
      assertThat(count()).isEqualTo(4)
      assertTrue(all { version -> with(version) { !deleted && versionNumber in listOf(0, 1, 2, 3) } })
    }

    assertThat(assessmentVersionRepository.findAllDeleted(assessment.uuid)).isEmpty()

    verify(exactly = 1) {
      telemetryService.assessmentUndeleted(
        withArg { assertEquals(assessment.uuid, it.uuid) },
        "user-id",
        withArg { assertTrue(it.all { version -> version.versionNumber in listOf(2, 3) }) },
      )
    }
  }

  @Test
  fun `it undeletes a specific range of assessment versions`() {
    val request = """
        {
          "versionFrom": 2,
          "versionTo": 3,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(2)

    assessmentRepository.findByUuid(assessment.uuid)?.assessmentVersions.orEmpty().run {
      assertThat(count()).isEqualTo(3)
      assertTrue(all { version -> with(version) { !deleted && versionNumber in listOf(0, 1, 2) } })
    }

    assessmentVersionRepository.findAllDeleted(assessment.uuid).run {
      assertThat(count()).isEqualTo(1)
      assertTrue(all { version -> with(version) { deleted && versionNumber in listOf(3) } })
    }

    verify(exactly = 1) {
      telemetryService.assessmentUndeleted(
        withArg { assertEquals(assessment.uuid, it.uuid) },
        "user-id",
        withArg {
          assertEquals(it.count(), 1)
          assertEquals(it.first().versionNumber, 2)
        },
      )
    }
  }
}
