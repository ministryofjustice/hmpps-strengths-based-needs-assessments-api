package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class CreateTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/oasys/assessment/create")
  inner class Create {
    private val endpoint = "/oasys/assessment/create"
    private lateinit var assessment: Assessment
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

    @BeforeEach
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
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_OASYS' is not present on the JWT`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = oasysAss1.oasysAssessmentPk,
        previousOasysAssessmentPk = oasysAss2.oasysAssessmentPk,
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
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = oasysAss2.oasysAssessmentPk,
        previousOasysAssessmentPk = oasysAss1.oasysAssessmentPk,
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isEqualTo(409)
    }

    @Test
    fun `it creates an assessment when only an OASys assessment PK provided and no assessment already exists`() {
      val newOasysPK = UUID.randomUUID().toString()

      val request = CreateAssessmentRequest(
        oasysAssessmentPk = newOasysPK,
        regionPrisonCode = "test-prison-code",
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk

      val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

      Assertions.assertThat(newOasysAss).isNotNull
      Assertions.assertThat(newOasysAss?.regionPrisonCode).isEqualTo(request.regionPrisonCode)
      Assertions.assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk }).isEqualTo(
        listOf(
          newOasysPK,
        ),
      )
    }
  }
}