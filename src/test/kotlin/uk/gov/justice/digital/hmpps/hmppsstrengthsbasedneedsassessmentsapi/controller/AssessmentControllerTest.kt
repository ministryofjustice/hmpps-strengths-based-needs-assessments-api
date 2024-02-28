package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AssociateAssessmentsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Transactional
@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController")
class AssessmentControllerTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val assessmentVersionRepository: AssessmentVersionRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  @Nested
  @DisplayName("/assessment/{assessmentUuid}")
  inner class GetAssessment {
    private val assessmentUuid = UUID.randomUUID().toString()
    private val endpoint = "/assessment/$assessmentUuid"

    private lateinit var assessment: Assessment
    private lateinit var latestVersion: AssessmentVersion
    private lateinit var latestValidatedVersion: AssessmentVersion
    private lateinit var oldValidatedVersion: AssessmentVersion
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

    @BeforeAll
    fun setUp() {
      assessment = Assessment(uuid = UUID.fromString(assessmentUuid))

      latestVersion = AssessmentVersion(
        tag = "unvalidated",
        assessment = assessment,
        answers = mapOf("q1" to Answer(value = "val1")),
        oasys_equivalent = mapOf("q1" to "1"),
      )
      latestValidatedVersion = AssessmentVersion(
        tag = "validated",
        assessment = assessment,
        createdAt = LocalDateTime.now().minusDays(1),
        answers = mapOf("q2" to Answer(value = "val2")),
        oasys_equivalent = mapOf("q2" to "2"),
      )
      oldValidatedVersion = AssessmentVersion(
        tag = "validated",
        assessment = assessment,
        createdAt = LocalDateTime.now().minusDays(3),
        answers = mapOf("q3" to Answer(value = "val3")),
        oasys_equivalent = mapOf("q3" to "3"),
      )

      oasysAss1 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      oasysAss2 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

      assessment.assessmentVersions = listOf(latestVersion, latestValidatedVersion, oldValidatedVersion)
      assessment.oasysAssessments = listOf(oasysAss1, oasysAss2)
      assessment.info = AssessmentFormInfo(formVersion = "1.0", assessment = assessment)

      assessmentRepository.save(assessment)
    }

    @Test
    fun `it returns Unauthorized when there is no JWT`() {
      webTestClient.get().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_READ' is not present on the JWT`() {
      webTestClient.get().uri(endpoint)
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
      val response = webTestClient.get().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isOk
        .expectBody(AssessmentResponse::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.metaData?.uuid).isEqualTo(assessment.uuid)
      assertThat(response?.metaData?.createdAt?.withNano(0)).isEqualTo(assessment.createdAt.withNano(0))
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf(oasysAss1.oasysAssessmentPk, oasysAss2.oasysAssessmentPk))
      assertThat(response?.metaData?.versionTag).isEqualTo(latestVersion.tag)
      assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(latestVersion.createdAt.withNano(0))
      assertThat(response?.metaData?.versionUuid).isEqualTo(latestVersion.uuid)
      assertThat(response?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
      assertThat(response?.assessment?.keys).isEqualTo(latestVersion.answers.keys)
      assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(latestVersion.answers.values.map { it.value })
      assertThat(response?.oasysEquivalent).isEqualTo(latestVersion.oasys_equivalent)
    }

    @Test
    fun `it returns an assessment for a given assessment UUID and tag`() {
      val response = webTestClient.get()
        .uri(
          UriComponentsBuilder
            .fromPath(endpoint)
            .queryParam("tag", "validated")
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
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf(oasysAss1.oasysAssessmentPk, oasysAss2.oasysAssessmentPk))
      assertThat(response?.metaData?.versionTag).isEqualTo(latestValidatedVersion.tag)
      assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(latestValidatedVersion.createdAt.withNano(0))
      assertThat(response?.metaData?.versionUuid).isEqualTo(latestValidatedVersion.uuid)
      assertThat(response?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
      assertThat(response?.assessment?.keys).isEqualTo(latestValidatedVersion.answers.keys)
      assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(latestValidatedVersion.answers.values.map { it.value })
      assertThat(response?.oasysEquivalent).isEqualTo(latestValidatedVersion.oasys_equivalent)
    }

    @Test
    fun `it returns an assessment for an assessment UUID and before a given date`() {
      val response = webTestClient.get()
        .uri(
          UriComponentsBuilder
            .fromPath(endpoint)
            .queryParam("until", LocalDateTime.now().minusDays(2).toEpochSecond(ZoneOffset.UTC))
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
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf(oasysAss1.oasysAssessmentPk, oasysAss2.oasysAssessmentPk))
      assertThat(response?.metaData?.versionTag).isEqualTo(oldValidatedVersion.tag)
      assertThat(response?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(oldValidatedVersion.createdAt.withNano(0))
      assertThat(response?.metaData?.versionUuid).isEqualTo(oldValidatedVersion.uuid)
      assertThat(response?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
      assertThat(response?.assessment?.keys).isEqualTo(oldValidatedVersion.answers.keys)
      assertThat(response?.assessment?.values?.map { it.value }).isEqualTo(oldValidatedVersion.answers.values.map { it.value })
      assertThat(response?.oasysEquivalent).isEqualTo(oldValidatedVersion.oasys_equivalent)
    }

    @Test
    fun `it returns an assessment for an assessment UUID and after a given date`() {
      webTestClient.get()
        .uri(
          UriComponentsBuilder
            .fromPath(endpoint)
            .queryParam("after", LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC))
            .build().toUriString(),
        )
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isNotFound
    }
  }

  @Nested
  @DisplayName("/assessment/{assessmentUuid}/answers")
  inner class Answers {
    private lateinit var assessment: Assessment
    private lateinit var assessmentVersion: AssessmentVersion

    @BeforeAll
    fun setUp() {
      assessment = Assessment()
      assessmentVersion = AssessmentVersion(
        tag = "unvalidated",
        assessment = assessment,
        answers = mapOf("q1" to Answer(value = "val1"), "q2" to Answer(value = "val2")),
      )
      assessment.assessmentVersions = listOf(assessmentVersion)
      assessment.oasysAssessments = listOf(OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment))
      assessment.info = AssessmentFormInfo(formVersion = "1.0", formName = "sbna-poc", assessment = assessment)

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
        tags = listOf("unvalidated"),
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
        tags = listOf("unvalidated"),
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
      val request = UpdateAssessmentAnswersRequest(
        tags = listOf("unvalidated"),
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
      val request = UpdateAssessmentAnswersRequest(
        tags = listOf("unvalidated"),
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

      assertThat(updatedAssessmentVersion.answers.keys).isEqualTo(setOf("q2", "field_name"))
      assertThat(updatedAssessmentVersion.answers.values.map { it.value }).isEqualTo(listOf("val2", "TEST"))
    }
  }

  @Nested
  @DisplayName("/assessment/associate")
  inner class Associate {
    private val endpoint = "/assessment/associate"
    private lateinit var assessment: Assessment
    private lateinit var oasysAss1: OasysAssessment
    private lateinit var oasysAss2: OasysAssessment

    @BeforeAll
    fun setUp() {
      assessment = Assessment()

      oasysAss1 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      oasysAss2 = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)

      assessment.assessmentVersions = listOf(AssessmentVersion(tag = "unvalidated", assessment = assessment))
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
