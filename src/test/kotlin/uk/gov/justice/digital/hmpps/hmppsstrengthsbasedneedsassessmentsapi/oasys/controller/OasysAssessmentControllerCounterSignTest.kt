package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class OasysAssessmentControllerCounterSignTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
  @Autowired
  val assessmentVersionAuditRepository: AssessmentVersionAuditRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/oasys/assessment/{oasysPK}/counter-sign")
  inner class CounterSign {
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessment: OasysAssessment
    private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}/counter-sign" }

    @BeforeEach
    fun setUp() {
      assessment = Assessment()
      oasysAssessment = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      assessment.assessmentVersions = listOf(
        AssessmentVersion(
          assessment = assessment,
          createdAt = LocalDateTime.now().minusDays(1),
          tag = Tag.UNSIGNED,
          versionNumber = 1,
          answers = mapOf("assessment_complete" to Answer(value = "YES")),
        ),
        AssessmentVersion(
          assessment = assessment,
          createdAt = LocalDateTime.now().minusDays(1),
          tag = Tag.UNVALIDATED,
          versionNumber = 0,
        ),
      )
      assessment.oasysAssessments = listOf(oasysAssessment)

      assessmentRepository.save(assessment)
    }

    @Test
    fun `it returns Unauthorized when there is no JWT`() {
      val request = """
        {
          "counterSignType": "SELF"
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
          "counterSignType": "SELF"
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
    fun `it creates and returns new signed version of the assessment`() {
      val request = """
        {
          "counterSignType": "SELF",
          "oasysUserID": "123",
          "oasysUserName": "John Doe"
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

      assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(3)
      val initialVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNSIGNED }
      val unvalidatedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNVALIDATED }
      val signedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.SELF_SIGNED }

      assertThat(initialVersion).isNotNull
      assertThat(unvalidatedVersion).isNotNull
      assertThat(signedVersion).isNotNull

      assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentVersion).isEqualTo(signedVersion?.versionNumber).isEqualTo(2)
      assertThat(response?.sentencePlanId).isNull()
      assertThat(response?.sentencePlanVersion).isNull()
    }

    @Test
    fun `it returns Conflict when the assessment is already signed`() {
      assessment.assessmentVersions = assessment.assessmentVersions + AssessmentVersion(
        assessment = assessment,
        answers = mapOf("assessment_complete" to Answer(value = "YES")),
        createdAt = LocalDateTime.now().minusHours(1),
        tag = Tag.SELF_SIGNED,
        versionNumber = 2,
      )
      assessmentRepository.save(assessment)

      val request = """
        {
          "counterSignType": "SELF"
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

      assertThat(response?.developerMessage).isEqualTo("The current assessment version is already SELF_SIGNED.")
    }
  }
}
