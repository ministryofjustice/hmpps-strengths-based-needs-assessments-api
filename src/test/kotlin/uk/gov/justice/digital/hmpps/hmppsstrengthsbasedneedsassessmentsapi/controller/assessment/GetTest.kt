package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("AssessmentController: /assessment/{assessmentUuid}")
class GetTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private val endpoint = { "/assessment/${assessment.uuid}" }

  private lateinit var assessment: Assessment
  private lateinit var latestVersion: AssessmentVersion
  private lateinit var previousVersion: AssessmentVersion

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

    assessment.assessmentVersions = mutableListOf(latestVersion, previousVersion)
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
    webTestClient.get().uri("/assessment/00000000-0000-0000-0000-000000000000")
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
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.createdAt?.withNano(0)).isEqualTo(assessment.createdAt.withNano(0))
    assertThat(response?.metaData?.versionTag).isEqualTo(latestVersion.tag)
    assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(latestVersion.createdAt.withNano(0))
    assertThat(response?.metaData?.versionUuid).isEqualTo(latestVersion.uuid)
    assertThat(response?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
    assertThat(response?.assessment?.keys).isEqualTo(latestVersion.answers.keys)
    assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(latestVersion.answers.values.map { it.value })
    assertThat(response?.oasysEquivalent).isEqualTo(latestVersion.oasysEquivalents)
  }

  @Test
  fun `it returns an assessment for a given assessment UUID and tag`() {
    val response = webTestClient.get()
      .uri(
        UriComponentsBuilder
          .fromPath(endpoint())
          .queryParam("tag", "UNSIGNED")
          .build().toUriString(),
      )
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isOk
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
    assertThat(response?.metaData?.createdAt?.withNano(0)).isEqualTo(assessment.createdAt.withNano(0))
    assertThat(response?.metaData?.versionTag).isEqualTo(latestVersion.tag)
    assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(
      latestVersion.createdAt.withNano(
        0,
      ),
    )
    assertThat(response?.metaData?.versionUuid).isEqualTo(latestVersion.uuid)
    assertThat(response?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
    assertThat(response?.assessment?.keys).isEqualTo(latestVersion.answers.keys)
    assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(latestVersion.answers.values.map { it.value })
    assertThat(response?.oasysEquivalent).isEqualTo(latestVersion.oasysEquivalents)
  }

  @Test
  fun `it returns an assessment for an assessment UUID and a given version number`() {
    val response = webTestClient.get()
      .uri(
        UriComponentsBuilder
          .fromPath(endpoint())
          .queryParam("versionNumber", 0)
          .build().toUriString(),
      )
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isOk
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.versionUuid).isEqualTo(previousVersion.uuid)
  }
}
