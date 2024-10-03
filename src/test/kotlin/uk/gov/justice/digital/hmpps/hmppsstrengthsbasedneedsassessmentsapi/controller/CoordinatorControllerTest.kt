package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.CreateAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.OasysPKGenerator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("Coordinator Controller")
class CoordinatorControllerTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,

) : IntegrationTest() {
  @Nested
  @DisplayName("Get Assessment")
  inner class GetAssessment {
    private val endpoint = { "/coordinator/assessment/${assessment.uuid}" }

    private lateinit var assessment: Assessment
    private lateinit var latestVersion: AssessmentVersion
    private lateinit var previousVersion: AssessmentVersion
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

    private fun endpointWith(uuid: String): String {
      return "/coordinator/assessment/$uuid"
    }

    @BeforeEach
    fun setUp() {
      assessment = Assessment(uuid = UUID.randomUUID())

      latestVersion = AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(1),
        answers = mapOf("q2" to Answer(value = "val2")),
        oasysEquivalents = mapOf("q2" to "2"),
        versionNumber = 1,
      )
      previousVersion = AssessmentVersion(
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(3),
        answers = mapOf("q3" to Answer(value = "val3")),
        oasysEquivalents = mapOf("q3" to "3"),
        versionNumber = 0,
      )

      oasysAss1 = OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment)
      oasysAss2 = OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment)

      assessment.assessmentVersions = listOf(latestVersion, previousVersion)
      assessment.oasysAssessments = listOf(oasysAss1, oasysAss2)
      assessment.info = AssessmentFormInfo(formVersion = "1.0", assessment = assessment)

      assessmentRepository.save(assessment)
    }

    @Test
    fun `it returns Unauthorized when there is no JWT`() {
      webTestClient.get().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_READ' is not present on the JWT`() {
      webTestClient.get().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it returns Not Found when no assessment exists for the given assessment UUID`() {
      webTestClient.get().uri(endpointWith("00000000-0000-0000-0000-000000000000"))
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `it returns an assessment for a given assessment UUID`() {
      val response = webTestClient.get().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isOk
        .expectBody(OasysAssessmentVersionResponse::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.sanAssessmentData?.metaData?.uuid).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentData?.metaData?.createdAt?.withNano(0)).isEqualTo(assessment.createdAt.withNano(0))
      assertThat(response?.sanAssessmentData?.metaData?.oasys_pks).containsExactlyInAnyOrder(
        oasysAss1.oasysAssessmentPk,
        oasysAss2.oasysAssessmentPk,
      )
      assertThat(response?.sanAssessmentData?.metaData?.versionTag).isEqualTo(latestVersion.tag)
      assertThat(response?.sanAssessmentData?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(latestVersion.createdAt.withNano(0))
      assertThat(response?.sanAssessmentData?.metaData?.versionUuid).isEqualTo(latestVersion.uuid)
      assertThat(response?.sanAssessmentData?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
      assertThat(response?.sanAssessmentData?.assessment?.keys).isEqualTo(latestVersion.answers.keys)
      assertThat(response?.sanAssessmentData?.assessment?.values?.map { it.value }).isEqualTo(latestVersion.answers.values.map { it.value })
      assertThat(response?.sanAssessmentData?.oasysEquivalent).isEqualTo(latestVersion.oasysEquivalents)
      assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentVersion).isEqualTo(latestVersion.versionNumber)
      assertThat(response?.lastUpdatedTimestamp?.withNano(0)).isEqualTo(latestVersion.updatedAt.withNano(0))
    }
  }

  @Nested
  @DisplayName("Create Assessment")
  inner class CreateAssessment {
    private lateinit var assessment: Assessment

    val endpoint = "/coordinator/assessment"

    @Test
    fun `it creates an assessment with audit information`() {
      val userDetails = UserDetails("11", "Test")
      val response = webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(CreateAssessmentRequest(userDetails = UserDetails("11", "Test")))
        .exchange()
        .expectStatus().isCreated
        .expectBody(CreateAssessmentResponse::class.java)
        .returnResult()
        .responseBody
      val newAssessment = assessmentRepository.findByUuid(response!!.id)
      assertThat(response).isEqualTo(CreateAssessmentResponse.from(newAssessment!!))
      assertThat(newAssessment.assessmentVersions.first().assessmentVersionAudit.first().userDetails).isEqualTo(userDetails)
    }
  }
}
