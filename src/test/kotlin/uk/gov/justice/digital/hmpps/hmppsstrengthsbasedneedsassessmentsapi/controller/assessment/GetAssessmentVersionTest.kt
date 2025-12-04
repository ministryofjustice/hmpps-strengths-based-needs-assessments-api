package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@DisplayName("Assessment Controller: /assessment/version/{assessmentVersionUuid}")
class GetAssessmentVersionTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment
  private lateinit var assessmentVersion1: AssessmentVersion
  private lateinit var assessmentVersion2: AssessmentVersion

  private val now = LocalDateTime.now()

  @BeforeEach
  fun setUp() {
    assessment = Assessment(uuid = UUID.randomUUID())

    assessmentVersion1 = AssessmentVersion(
      assessment = assessment,
      updatedAt = now.minusDays(4),
      answers = mapOf("q2" to Answer(value = "val2")),
      oasysEquivalents = mapOf("q2" to "2"),
      versionNumber = 0,

    )
    assessmentVersion2 = AssessmentVersion(
      assessment = assessment,
      updatedAt = now.minusDays(1),
      answers = assessmentVersion1.answers + mapOf("q3" to Answer(value = "val3")),
      oasysEquivalents = assessmentVersion1.oasysEquivalents + mapOf("q3" to "3"),
      versionNumber = 1,
    )
    assessment.assessmentVersions = listOf(assessmentVersion1, assessmentVersion2)
    assessment.info = AssessmentFormInfo(formVersion = "1.0", assessment = assessment)
    assessmentRepository.save(assessment)
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    webTestClient.get().uri("/assessment/version/${assessmentVersion1.uuid}")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_READ' is not present on the JWT`() {
    webTestClient.get().uri("/assessment/version/${assessmentVersion2.uuid}")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun `it returns Not Found when no assessment version exists with UUID provided`() {
    webTestClient.get().uri("/assessment/version/00000000-0000-0000-0000-000000000000")
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `it returns correct assessment version when UUID provided`() {
    val response = webTestClient.get().uri("/assessment/version/${assessmentVersion2.uuid}")
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isOk
      .expectBody(AssessmentResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.metaData?.versionUuid).isEqualTo(assessmentVersion2.uuid)
    assertThat(response?.metaData?.versionCreatedAt?.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(assessmentVersion2.createdAt.truncatedTo(ChronoUnit.SECONDS))
    assertThat(response?.metaData?.versionUpdatedAt?.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(assessmentVersion2.updatedAt.truncatedTo(ChronoUnit.SECONDS))
    assertThat(response?.metaData?.versionTag).isEqualTo(assessmentVersion2.tag)
    assertThat(response?.assessment?.keys).isEqualTo(assessmentVersion2.answers.keys)
    assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(assessmentVersion2.answers.values.map { it.value })
    assertThat(response?.oasysEquivalent).isEqualTo(assessmentVersion2.oasysEquivalents)
  }
}
