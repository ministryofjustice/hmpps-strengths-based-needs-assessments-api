package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController: /oasys/assessment/{oasysPK}/rollback")
class RollbackTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private fun endpoint(oasysAssessmentPk: String? = null) =
    "/oasys/assessment/${oasysAssessmentPk ?: oasysAssessment.oasysAssessmentPk}/rollback"

  private lateinit var assessment: Assessment
  private lateinit var oasysAssessment: OasysAssessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()

    oasysAssessment = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

    assessment.oasysAssessments = listOf(oasysAssessment)

    assessmentRepository.save(assessment)
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    val request = """
        {
          "sanVersionNumber": 1,
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
          "sanVersionNumber": 1,
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
          "sanVersionNumber": 1,
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
  fun `it returns Not Found when the OASys PK does not exist`() {
    val request = """
        {
          "sanVersionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint("non-existent-assessment"))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.developerMessage).isEqualTo("No OASys assessment found for PK non-existent-assessment")
  }

  @Test
  fun `it rolls back an assessment for a given OASys PK`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(versionNumber = 1, assessment = assessment, tag = Tag.AWAITING_COUNTERSIGN),
    )

    assessmentRepository.save(assessment)

    val request = """
        {
          "sanVersionNumber": 1,
          "userDetails": { "id": "user-id", "name": "John Doe" }
        }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(OasysAssessmentResponse::class.java)
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

    assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
    assertThat(response?.sanAssessmentVersion).isEqualTo(1)
    assertThat(response?.sentencePlanId).isNull()
    assertThat(response?.sentencePlanVersion).isNull()
  }

  @Test
  fun `it returns Conflict when the assessment for the given OASys PK is not in the correct state`() {
    assessment.assessmentVersions = listOf(
      AssessmentVersion(versionNumber = 1, assessment = assessment, tag = Tag.UNSIGNED),
    )

    assessmentRepository.save(assessment)

    val request = """
        {
          "sanVersionNumber": 1,
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
