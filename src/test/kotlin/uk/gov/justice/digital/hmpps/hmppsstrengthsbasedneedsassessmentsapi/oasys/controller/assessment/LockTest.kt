package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class LockTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/oasys/assessment/{oasysPK}/lock")
  inner class Lock {
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessment: OasysAssessment
    private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}/lock" }

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
          answers = mapOf("q1" to Answer(value = "val1")),
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
      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_OASYS' is not present on the JWT`() {
      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it updates and returns the version of the assessment as Locked`() {
      val response = webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .exchange()
        .expectStatus().isOk
        .expectBody(OasysAssessmentResponse::class.java)
        .returnResult()
        .responseBody

      val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

      Assertions.assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)
      val initialVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNSIGNED }
      val unvalidatedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNVALIDATED }
      val lockedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.LOCKED_INCOMPLETE }

      Assertions.assertThat(initialVersion).isNull()
      Assertions.assertThat(unvalidatedVersion).isNotNull
      Assertions.assertThat(lockedVersion).isNotNull

      Assertions.assertThat(lockedVersion!!.assessmentVersionAudit.count()).isEqualTo(1)

      val audit = lockedVersion.assessmentVersionAudit.first()
      Assertions.assertThat(audit.statusFrom).isEqualTo(Tag.UNSIGNED)
      Assertions.assertThat(audit.statusTo).isEqualTo(Tag.LOCKED_INCOMPLETE)
      Assertions.assertThat(audit.userDetails.id).isEqualTo("")
      Assertions.assertThat(audit.userDetails.name).isEqualTo("")

      Assertions.assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      Assertions.assertThat(response?.sanAssessmentVersion).isEqualTo(lockedVersion.versionNumber).isEqualTo(1)
      Assertions.assertThat(response?.sentencePlanId).isNull()
      Assertions.assertThat(response?.sentencePlanVersion).isNull()
    }

    @Test
    fun `it returns Conflict when the assessment is already locked`() {
      assessment.assessmentVersions = assessment.assessmentVersions + AssessmentVersion(
        assessment = assessment,
        createdAt = LocalDateTime.now().minusHours(1),
        tag = Tag.LOCKED_INCOMPLETE,
        versionNumber = 2,
      )
      assessmentRepository.save(assessment)

      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .exchange()
        .expectStatus().isEqualTo(409)
    }
  }
}