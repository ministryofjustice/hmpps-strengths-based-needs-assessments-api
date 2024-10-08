package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/answers")
class AnswersTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()
    assessment.info = AssessmentFormInfo(formVersion = "1.0", assessment = assessment)
    assessmentRepository.save(assessment)
  }

  private fun endpointWith(assessmentUUID: UUID): String {
    return "/assessment/$assessmentUUID/answers"
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    webTestClient.post().uri(endpointWith(UUID.randomUUID()))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_WRITE' is not present on the JWT`() {
    val request = UpdateAssessmentAnswersRequest(
      answersToAdd = emptyMap(),
      answersToRemove = emptyList(),
    )

    webTestClient.post().uri(endpointWith(UUID.randomUUID()))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation())
      .bodyValue(request)
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun `it returns Not Found when the assessment does not exist`() {
    val request = UpdateAssessmentAnswersRequest(
      answersToAdd = emptyMap(),
      answersToRemove = emptyList(),
    )

    webTestClient.post().uri(endpointWith(UUID.fromString("00000000-0000-0000-0000-000000000000")))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `it adds answers for an assessment`() {
    val assessmentVersion = AssessmentVersion(
      assessment = assessment,
      versionNumber = 0,
      answers = mapOf(
        "q1" to Answer(value = "val1"),
        "q2" to Answer(value = "val2"),
      ),
    )

    assessment.assessmentVersions = listOf(assessmentVersion)
    assessmentRepository.save(assessment)

    val request = UpdateAssessmentAnswersRequest(
      answersToAdd = mapOf("field_name" to Answer(type = AnswerType.TEXT, description = "Field", value = "TEST")),
      answersToRemove = emptyList(),
    )

    webTestClient.post().uri(endpointWith(assessment.uuid))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val updatedAssessmentVersion = assessmentVersionRepository.findByUuid(assessmentVersion.uuid)

    assertThat(updatedAssessmentVersion.answers.keys).isEqualTo(setOf("q1", "q2", "field_name"))
    assertThat(updatedAssessmentVersion.answers.values.map { it.value }).isEqualTo(listOf("val1", "val2", "TEST"))
  }

  @Test
  fun `it removes answers for an assessment`() {
    val assessmentVersion = AssessmentVersion(
      assessment = assessment,
      versionNumber = 0,
      answers = mapOf(
        "q1" to Answer(value = "val1"),
        "q2" to Answer(value = "val2"),
      ),
    )

    assessment.assessmentVersions = listOf(assessmentVersion)
    assessmentRepository.save(assessment)

    val request = UpdateAssessmentAnswersRequest(
      answersToAdd = emptyMap(),
      answersToRemove = listOf("q1"),
    )

    webTestClient.post().uri(endpointWith(assessment.uuid))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val updatedAssessmentVersion = assessmentVersionRepository.findByUuid(assessmentVersion.uuid)

    assertThat(updatedAssessmentVersion.answers.keys).isEqualTo(setOf("q2"))
    assertThat(updatedAssessmentVersion.answers.values.map { it.value }).isEqualTo(listOf("val2"))
  }

  @Test
  fun `it clones from the latest locked version and adds answers for an assessment`() {
    val assessmentVersion = AssessmentVersion(
      assessment = assessment,
      tag = Tag.LOCKED_INCOMPLETE,
      versionNumber = 0,
      answers = mapOf(
        "q1" to Answer(value = "val1"),
        "q2" to Answer(value = "val2"),
      ),
    )

    assessment.assessmentVersions = listOf(assessmentVersion)
    assessmentRepository.save(assessment)

    val request = UpdateAssessmentAnswersRequest(
      answersToAdd = mapOf("field_name" to Answer(type = AnswerType.TEXT, description = "Field", value = "TEST")),
      answersToRemove = emptyList(),
    )

    webTestClient.post().uri(endpointWith(assessment.uuid))
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val spec = Specification { root, query, builder ->
      query.where(
        builder.equal(root.get(AssessmentVersion_.assessment).get(Assessment_.uuid), assessment.uuid),
        builder.equal(root.get(AssessmentVersion_.versionNumber), 1),
      ).restriction
    }

    val clonedAssessmentVersion = assessmentVersionRepository.findOne(spec).get()

    assertThat(clonedAssessmentVersion.tag).isEqualTo(Tag.UNSIGNED)
    assertThat(clonedAssessmentVersion.answers.keys).isEqualTo(setOf("q1", "q2", "field_name"))
    assertThat(clonedAssessmentVersion.answers.values.map { it.value })
      .isEqualTo(listOf("val1", "val2", "TEST"))
  }
}
