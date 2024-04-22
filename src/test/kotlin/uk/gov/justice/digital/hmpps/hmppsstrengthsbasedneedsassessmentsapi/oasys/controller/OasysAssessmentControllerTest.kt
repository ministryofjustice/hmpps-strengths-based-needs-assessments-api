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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
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
  @DisplayName("/assessment/create")
  inner class Associate {
    private val endpoint = "/oasys/assessment/create"
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
      val request = AssociateAssessmentRequest(
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
      val request = AssociateAssessmentRequest(
        oasysAssessmentPk = oasysAss2.oasysAssessmentPk,
        previousOasysAssessmentPk = oasysAss1.oasysAssessmentPk,
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isEqualTo(409)
    }

    @Test
    fun `it creates an assessment when only an OASys assessment PK provided and no assessment already exists`() {
      val newOasysPK = UUID.randomUUID().toString()

      val request = AssociateAssessmentRequest(
        oasysAssessmentPk = newOasysPK,
      )

      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk

      val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

      assertThat(newOasysAss).isNotNull
      assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk }).isEqualTo(
        listOf(
          newOasysPK,
        ),
      )
    }
  }

  @Nested
  @DisplayName("/assessment/{oasysPK}/lock")
  inner class Lock {
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessment: OasysAssessment
    private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}/lock" }

    @BeforeAll
    fun setUp() {
      assessment = Assessment()
      oasysAssessment = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      assessment.assessmentVersions = listOf(
        AssessmentVersion(
          assessment = assessment,
          createdAt = LocalDateTime.now().minusDays(1),
          tag = Tag.VALIDATED,
        ),
      )
      assessment.oasysAssessments = listOf(oasysAssessment)
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
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_WRITE' is not present on the JWT`() {
      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it creates and returns new locked version of the assessment`() {
      val response = webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .exchange()
        .expectStatus().isOk
        .expectBody(AssessmentResponse::class.java)
        .returnResult()
        .responseBody

      val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

      assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)
      val initialVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.VALIDATED }
      val lockedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.LOCKED }

      assertThat(initialVersion).isNotNull
      assertThat(lockedVersion).isNotNull

      assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
      assertThat(response?.metaData?.createdAt?.withNano(0)).isEqualTo(assessment.createdAt.withNano(0))
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf(oasysAssessment.oasysAssessmentPk))
      assertThat(response?.metaData?.versionTag).isEqualTo(Tag.LOCKED)
      assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(lockedVersion!!.createdAt.withNano(0))
      assertThat(response?.metaData?.versionUuid).isEqualTo(lockedVersion.uuid)
    }

    @Test
    fun `it returns Conflict when the assessment is already locked`() {
      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .exchange()
        .expectStatus().isEqualTo(409)
    }
  }
}
