package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.OasysPKGenerator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController: /oasys/assessment/{oasysPK}")
class GetTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private lateinit var oasysAssessment: OasysAssessment
  private lateinit var latestVersion: AssessmentVersion
  private lateinit var previousVersion: AssessmentVersion

  private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}" }

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    oasysAssessment = OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment)
    latestVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(1),
      answers = mapOf("q2" to Answer(value = "val2")),
      oasysEquivalents = mapOf("q2" to "2"),
      versionNumber = 2,
    )
    previousVersion = AssessmentVersion(
      assessment = assessment,
      updatedAt = LocalDateTime.now().minusDays(3),
      answers = mapOf("q3" to Answer(value = "val3")),
      oasysEquivalents = mapOf("q3" to "3"),
      versionNumber = 1,
    )

    assessment.assessmentVersions = listOf(latestVersion, previousVersion)
    assessment.oasysAssessments = listOf(oasysAssessment)
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
    webTestClient.get().uri("/oasys/assessment/non-existent-pk")
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `it returns the latest assessment for a given oasysAssessmentPk`() {
    val response = webTestClient.get().uri(endpoint())
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isOk
      .expectBody(OasysAssessmentVersionResponse::class.java)
      .returnResult()
      .responseBody

    Assertions.assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
    Assertions.assertThat(response?.sanAssessmentVersion).isEqualTo(latestVersion.versionNumber)
    Assertions.assertThat(response?.lastUpdatedTimestamp?.withNano(0))
      .isEqualTo(latestVersion.updatedAt.withNano(0))
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.uuid).isEqualTo(assessment.uuid)
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.createdAt?.withNano(0)).isEqualTo(
      assessment.createdAt.withNano(
        0,
      ),
    )
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.oasys_pks)
      .containsExactly(oasysAssessment.oasysAssessmentPk)
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.versionTag).isEqualTo(latestVersion.tag)
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(
      latestVersion.createdAt.withNano(
        0,
      ),
    )
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.versionUuid).isEqualTo(latestVersion.uuid)
    Assertions.assertThat(response?.sanAssessmentData?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
    Assertions.assertThat(response?.sanAssessmentData?.assessment?.keys).isEqualTo(latestVersion.answers.keys)
    Assertions.assertThat(response?.sanAssessmentData?.assessment?.values?.map { it.value })
      .isEqualTo(latestVersion.answers.values.map { it.value })
    Assertions.assertThat(response?.sanAssessmentData?.oasysEquivalent)
      .isEqualTo(latestVersion.oasysEquivalents)
  }
}
