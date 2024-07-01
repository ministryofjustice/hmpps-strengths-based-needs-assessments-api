package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.Message
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController: /oasys/assessment/merge")
class MergeTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  private val endpoint = "/oasys/assessment/merge"
  private lateinit var assessment: Assessment
  private lateinit var oasysAssessmentA: OasysAssessment
  private lateinit var oasysAssessmentB: OasysAssessment
  private lateinit var oasysAssessmentC: OasysAssessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()

    oasysAssessmentA = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
    oasysAssessmentB = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
    oasysAssessmentC = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

    assessment.oasysAssessments = listOf(oasysAssessmentA, oasysAssessmentB, oasysAssessmentC)

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
        "merge": [
          {
            "newOasysAssessmentPK": "NEW_OASYS_ASSESSMENT_PK",
            "oldOasysAssessmentPK": "${oasysAssessmentA.oasysAssessmentPk}"
          }
        ],
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
        "merge": [
          {
            "newOasysAssessmentPK": "NEW_OASYS_ASSESSMENT_PK",
            "oldOasysAssessmentPK": "FOO_OASYS_ASSESSMENT"
          }
        ],
        "userDetails": { "id": "user-id", "name": "John Doe" },
        "foo": "bar"
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
  fun `it returns Not Found when the old OASys PK does not exist`() {
    val request = """
      {
        "merge": [
          {
            "newOasysAssessmentPK": "NEW_OASYS_ASSESSMENT_PK",
            "oldOasysAssessmentPK": "FOO_OASYS_ASSESSMENT"
          }
        ],
        "userDetails": { "id": "user-id", "name": "John Doe" }
      }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `it returns Conflict when the new OASys PK already exists`() {
    val request = """
      {
        "merge": [
          {
            "newOasysAssessmentPK": "${oasysAssessmentB.oasysAssessmentPk}",
            "oldOasysAssessmentPK": "${oasysAssessmentA.oasysAssessmentPk}"
          }
        ],
        "userDetails": { "id": "user-id", "name": "John Doe" }
      }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isEqualTo(409)
  }

  @Test
  fun `it transfers association with an assessment to the new PK`() {
    val newAssessmentPkX = UUID.randomUUID().toString()
    val newAssessmentPkY = UUID.randomUUID().toString()

    val request = """
      {
        "merge": [
          {
            "newOasysAssessmentPK": "$newAssessmentPkX",
            "oldOasysAssessmentPK": "${oasysAssessmentA.oasysAssessmentPk}"
          },
          {
            "newOasysAssessmentPK": "$newAssessmentPkY",
            "oldOasysAssessmentPK": "${oasysAssessmentB.oasysAssessmentPk}"
          }
        ],
        "userDetails": { "id": "user-id", "name": "John Doe" }
      }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk
      .expectBody(Message::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.message).isEqualTo("Successfully processed all 2 merge elements")

    listOf(oasysAssessmentA, oasysAssessmentB).forEach {
      with(oasysAssessmentRepository.findByOasysAssessmentPk(it.oasysAssessmentPk)) {
        assertThat(this).isNull()
      }
    }

    with(oasysAssessmentRepository.findByOasysAssessmentPk(newAssessmentPkX)) {
      assertThat(this).isNotNull
      assertThat(this?.assessment?.uuid).isEqualTo(oasysAssessmentA.assessment.uuid)
    }

    with(oasysAssessmentRepository.findByOasysAssessmentPk(newAssessmentPkY)) {
      assertThat(this).isNotNull
      assertThat(this?.assessment?.uuid).isEqualTo(oasysAssessmentB.assessment.uuid)
    }
  }

  @Test
  fun `it rolls back on failure`() {
    val newAssessmentPkX = UUID.randomUUID().toString()
    val newAssessmentPkY = UUID.randomUUID().toString()

    val request = """
      {
        "merge": [
          {
            "newOasysAssessmentPK": "$newAssessmentPkX",
            "oldOasysAssessmentPK": "${oasysAssessmentA.oasysAssessmentPk}"
          },
          {
            "newOasysAssessmentPK": "${oasysAssessmentC.oasysAssessmentPk}",
            "oldOasysAssessmentPK": "${oasysAssessmentB.oasysAssessmentPk}"
          }
        ],
        "userDetails": { "id": "user-id", "name": "John Doe" }
      }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isEqualTo(409)

    listOf(oasysAssessmentA, oasysAssessmentB, oasysAssessmentC).forEach {
      with(oasysAssessmentRepository.findByOasysAssessmentPk(it.oasysAssessmentPk)) {
        assertThat(this).isNotNull
      }
    }

    listOf(newAssessmentPkX, newAssessmentPkY).forEach {
      with(oasysAssessmentRepository.findByOasysAssessmentPk(it)) {
        assertThat(this).isNull()
      }
    }
  }
}
