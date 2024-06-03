package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.Message
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.TransferAssociationRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class OasysAssessmentControllerMergeTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/oasys/assessment/merge")
  inner class Merge {
    private val endpoint = "/oasys/assessment/merge"
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessmentA: OasysAssessment
    private lateinit var oasysAssessmentB: OasysAssessment

    @BeforeEach
    fun setUp() {
      assessment = Assessment()

      oasysAssessmentA = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      oasysAssessmentB = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

      assessment.oasysAssessments = listOf(oasysAssessmentA, oasysAssessmentB)

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
      val request = listOf(
        TransferAssociationRequest(
          newOasysAssessmentPK = "NEW_OASYS_ASSESSMENT_PK",
          oldOasysAssessmentPK = oasysAssessmentA.oasysAssessmentPk,
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .bodyValue(request)
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it returns Not Found when the old OASys PK does not exist`() {
      val request = listOf(
        TransferAssociationRequest(
          newOasysAssessmentPK = "NEW_OASYS_ASSESSMENT_PK",
          oldOasysAssessmentPK = "FOO_OASYS_ASSESSMENT",
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `it returns Conflict when the new OASys PK already exists`() {
      val request = listOf(
        TransferAssociationRequest(
          newOasysAssessmentPK = oasysAssessmentB.oasysAssessmentPk,
          oldOasysAssessmentPK = oasysAssessmentA.oasysAssessmentPk,
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isEqualTo(409)
    }

    @Test
    fun `it transfers association with an assessment to the new PK`() {
      val newAssessmentPk = UUID.randomUUID().toString()

      val request = listOf(
        TransferAssociationRequest(
          newOasysAssessmentPK = newAssessmentPk,
          oldOasysAssessmentPK = oasysAssessmentA.oasysAssessmentPk,
        ),
      )

      val response = webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(Message::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.message).isEqualTo("Successfully processed all 1 merge elements")

      val oldAssessment = oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentA.oasysAssessmentPk)
      assertThat(oldAssessment).isNull()

      val newAssessment = oasysAssessmentRepository.findByOasysAssessmentPk(newAssessmentPk)
      assertThat(newAssessment).isNotNull
      assertThat(newAssessment?.assessment?.uuid).isEqualTo(oasysAssessmentA.assessment.uuid)
    }
  }
}
