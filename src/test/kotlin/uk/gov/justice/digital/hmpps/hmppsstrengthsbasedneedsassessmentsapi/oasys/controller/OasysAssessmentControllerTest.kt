package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@Transactional
@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class OasysAssessmentControllerTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/assessment/associate")
  inner class Associate {
    private val endpoint = "/oasys/assessment/associate"
    private lateinit var assessment: Assessment
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

    @BeforeAll
    fun setUp() {
      assessment = Assessment()

      oasysAss1 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      oasysAss2 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

      assessment.assessmentVersions = listOf(AssessmentVersion(assessment = assessment))
      assessment.oasysAssessments = listOf(oasysAss1, oasysAss2)

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
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_WRITE' is not present on the JWT`() {
      val request = AssociateAssessmentsRequest(
        associate = listOf(
          AssociateAssessmentRequest(
            oasysAssessmentPk = oasysAss1.oasysAssessmentPk,
            oldOasysAssessmentPk = oasysAss2.oasysAssessmentPk,
          ),
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
    fun `it returns Conflict when the association already exists`() {
      val request = AssociateAssessmentsRequest(
        associate = listOf(
          AssociateAssessmentRequest(
            oasysAssessmentPk = oasysAss2.oasysAssessmentPk,
            oldOasysAssessmentPk = oasysAss1.oasysAssessmentPk,
          ),
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isEqualTo(409)
    }

    @Test
    fun `it creates an association with an existing OASys assessment PK, for multiple pairs`() {
      val newOasysPk1 = UUID.randomUUID().toString()
      val newOasysPk2 = UUID.randomUUID().toString()

      val request = AssociateAssessmentsRequest(
        associate = listOf(
          AssociateAssessmentRequest(
            oasysAssessmentPk = newOasysPk1,
            oldOasysAssessmentPk = oasysAss1.oasysAssessmentPk,
          ),
          AssociateAssessmentRequest(
            oasysAssessmentPk = newOasysPk2,
            oldOasysAssessmentPk = oasysAss2.oasysAssessmentPk,
          ),
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk

      val newOasysAss1 = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPk1)
      val newOasysAss2 = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPk2)
      val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

      assertThat(newOasysAss1).isNotNull()
      assertThat(newOasysAss2).isNotNull()
      assertThat(updatedAssessment!!.oasysAssessments.map { it.oasysAssessmentPk }).isEqualTo(
        listOf(
          oasysAss1.oasysAssessmentPk,
          oasysAss2.oasysAssessmentPk,
          newOasysPk1,
          newOasysPk2,
        ),
      )
    }

    @Test
    fun `it creates an assessment when only an OASys assessment PK provided and no assessment already exists`() {
      val newOasysPK = UUID.randomUUID().toString()

      val request = AssociateAssessmentsRequest(
        associate = listOf(
          AssociateAssessmentRequest(
            oasysAssessmentPk = newOasysPK,
          ),
        ),
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk

      val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

      assertThat(newOasysAss).isNotNull()
      assertThat(newOasysAss!!.assessment.oasysAssessments.map { it.oasysAssessmentPk }).isEqualTo(
        listOf(
          newOasysPK,
        ),
      )
    }
  }
}
