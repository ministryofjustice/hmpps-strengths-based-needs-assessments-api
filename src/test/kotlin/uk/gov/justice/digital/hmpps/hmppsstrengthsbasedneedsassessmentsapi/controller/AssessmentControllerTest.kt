package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.VersionedAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.OasysPKGenerator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.toVersionedAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController")
class AssessmentControllerTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/assessment/{assessmentUuid}")
  inner class GetAssessment {
    private val endpoint = { "/assessment/${assessment.uuid}" }

    private lateinit var assessment: Assessment
    private lateinit var latestVersion: AssessmentVersion
    private lateinit var previousVersion: AssessmentVersion
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

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
      assertThat(response?.metaData?.oasys_pks).containsExactlyInAnyOrder(
        oasysAss1.oasysAssessmentPk,
        oasysAss2.oasysAssessmentPk,
      )
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
      assertThat(response?.metaData?.oasys_pks).containsExactlyInAnyOrder(
        oasysAss1.oasysAssessmentPk,
        oasysAss2.oasysAssessmentPk,
      )
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

  @Nested
  @DisplayName("/assessment/{assessmentUuid}/answers")
  inner class Answers {
    private lateinit var assessment: Assessment

    @BeforeEach
    fun setUp() {
      assessment = Assessment()
      assessment.oasysAssessments =
        listOf(OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment))
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

    @Test
    fun `it creates a SAN`() {
      val objectMapper: ObjectMapper = jacksonObjectMapper()
      val responseJson = webTestClient.post().uri("/assessment/san")
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .bodyValue(UserDetails("11", "Test"))
        .exchange()
        .expectStatus().isCreated
        .expectBody(VersionedAssessment::class.java)
        .returnResult()
        .responseBodyContent
      val response = objectMapper.readValue<VersionedAssessment>(responseJson!!)
      val newAssessment = assessmentRepository.findByUuid(response.id)
      assertThat(response).isEqualTo(newAssessment?.toVersionedAssessment())
    }
  }
}
