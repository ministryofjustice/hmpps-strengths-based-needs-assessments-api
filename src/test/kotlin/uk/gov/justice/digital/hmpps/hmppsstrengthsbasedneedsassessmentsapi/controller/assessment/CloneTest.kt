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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.TelemetryService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import kotlin.test.assertEquals

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/clone")
class CloneTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val telemetryService: TelemetryService,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private val endpoint = { "/assessment/${assessment.uuid}/clone" }

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessmentRepository.save(assessment)
    clearAllMocks()
    every { telemetryService.assessmentCreated(any(), any(), any()) } just Runs
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

    verify(exactly = 0) { telemetryService.assessmentCreated(any(), any(), any()) }
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

    verify(exactly = 0) { telemetryService.assessmentCreated(any(), any(), any()) }
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

    verify(exactly = 0) { telemetryService.assessmentCreated(any(), any(), any()) }
  }

  @Test
  fun `it clones and returns the latest version of the assessment`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(2),
        versionNumber = 0,
      ),
      AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(1),
        versionNumber = 1,
        tag = Tag.LOCKED_INCOMPLETE,
        answers = mapOf("q1" to Answer(value = "val1")),
      ),
    )
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
      .expectStatus().isCreated
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

    assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(3)

    val clonedVersion = updatedAssessment.assessmentVersions.find { it.versionNumber == 2 }
    val previousVersion = updatedAssessment.assessmentVersions.find { it.versionNumber == 1 }

    assertThat(clonedVersion).isNotNull
    assertThat(previousVersion).isNotNull
    assertThat(clonedVersion?.tag).isEqualTo(Tag.UNSIGNED)

    assertThat(clonedVersion!!.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = clonedVersion.assessmentVersionAudit.first()
    assertThat(audit.statusFrom).isNull()
    assertThat(audit.statusTo).isNull()
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(clonedVersion.versionNumber).isEqualTo(2)

    verify(exactly = 1) {
      telemetryService.assessmentCreated(
        withArg { assertEquals(clonedVersion.uuid, it.uuid) },
        "user-id",
        previousVersion?.versionNumber,
      )
    }
  }

  @Test
  fun `it clones from the latest non-deleted version of the assessment`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(2),
        versionNumber = 0,
      ),
      AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(1),
        versionNumber = 1,
        tag = Tag.LOCKED_INCOMPLETE,
        answers = mapOf("q1" to Answer(value = "val1")),
        deleted = true,
      ),
    )
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
      .expectStatus().isCreated
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

    assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)

    val clonedVersion = updatedAssessment.assessmentVersions.find { it.versionNumber == 2 }
    val previousVersion = updatedAssessment.assessmentVersions.find { it.versionNumber == 0 }

    assertThat(clonedVersion).isNotNull
    assertThat(previousVersion).isNotNull
    assertThat(clonedVersion?.tag).isEqualTo(Tag.UNSIGNED)

    assertThat(clonedVersion!!.assessmentVersionAudit.count()).isEqualTo(1)

    val audit = clonedVersion.assessmentVersionAudit.first()
    assertThat(audit.statusFrom).isNull()
    assertThat(audit.statusTo).isNull()
    assertThat(audit.userDetails.id).isEqualTo("user-id")
    assertThat(audit.userDetails.name).isEqualTo("John Doe")

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.versionNumber).isEqualTo(clonedVersion.versionNumber).isEqualTo(2)

    verify(exactly = 1) {
      telemetryService.assessmentCreated(
        withArg { assertEquals(clonedVersion.uuid, it.uuid) },
        "user-id",
        previousVersion?.versionNumber,
      )
    }
  }
}
