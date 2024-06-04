package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.response.OasysAssessmentVersionResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController")
class OasysAssessmentControllerTest(
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

      assertThat(newOasysAss).isNotNull
      assertThat(newOasysAss?.regionPrisonCode).isEqualTo(request.regionPrisonCode)
      assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk }).isEqualTo(
        listOf(
          newOasysPK,
        ),
      )
    }
  }

  @Nested
  @DisplayName("/oasys/assessment/{oasysPK}")
  inner class Get {
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessment: OasysAssessment
    private lateinit var latestVersion: AssessmentVersion
    private lateinit var latestValidatedVersion: AssessmentVersion
    private lateinit var oldValidatedVersion: AssessmentVersion

    private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}" }

    @BeforeEach
    fun setUp() {
      assessment = Assessment()
      oasysAssessment = OasysAssessment(oasysAssessmentPk = UUID.randomUUID().toString(), assessment = assessment)
      latestVersion = AssessmentVersion(
        assessment = assessment,
        answers = mapOf("q1" to Answer(value = "val1")),
        oasys_equivalent = mapOf("q1" to "1"),
        versionNumber = 3,
      )
      latestValidatedVersion = AssessmentVersion(
        tag = Tag.UNSIGNED,
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(1),
        answers = mapOf("q2" to Answer(value = "val2")),
        oasys_equivalent = mapOf("q2" to "2"),
        versionNumber = 2,
      )
      oldValidatedVersion = AssessmentVersion(
        tag = Tag.UNSIGNED,
        assessment = assessment,
        updatedAt = LocalDateTime.now().minusDays(3),
        answers = mapOf("q3" to Answer(value = "val3")),
        oasys_equivalent = mapOf("q3" to "3"),
        versionNumber = 1,
      )

      assessment.assessmentVersions = listOf(latestVersion, latestValidatedVersion, oldValidatedVersion)
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
      webTestClient.get().uri("/oasys/assessment/some-non-existent-id")
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `it returns the latest validated assessment for a given oasysAssessmentPk`() {
      val response = webTestClient.get().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isOk
        .expectBody(OasysAssessmentVersionResponse::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentVersion).isEqualTo(latestValidatedVersion.versionNumber)
      assertThat(response?.lastUpdatedTimestamp?.withNano(0)).isEqualTo(latestValidatedVersion.updatedAt.withNano(0))
      assertThat(response?.sanAssessmentData?.metaData?.uuid).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentData?.metaData?.createdAt?.withNano(0)).isEqualTo(
        assessment.createdAt.withNano(
          0,
        ),
      )
      assertThat(response?.sanAssessmentData?.metaData?.oasys_pks).containsExactly(oasysAssessment.oasysAssessmentPk)
      assertThat(response?.sanAssessmentData?.metaData?.versionTag).isEqualTo(latestValidatedVersion.tag)
      assertThat(response?.sanAssessmentData?.metaData?.versionCreatedAt?.withNano(0)).isEqualTo(
        latestValidatedVersion.createdAt.withNano(
          0,
        ),
      )
      assertThat(response?.sanAssessmentData?.metaData?.versionUuid).isEqualTo(latestValidatedVersion.uuid)
      assertThat(response?.sanAssessmentData?.metaData?.formVersion).isEqualTo(assessment.info!!.formVersion)
      assertThat(response?.sanAssessmentData?.assessment?.keys).isEqualTo(latestValidatedVersion.answers.keys)
      assertThat(response?.sanAssessmentData?.assessment?.values?.map { it.value })
        .isEqualTo(latestValidatedVersion.answers.values.map { it.value })
      assertThat(response?.sanAssessmentData?.oasysEquivalent).isEqualTo(latestValidatedVersion.oasys_equivalent)
    }
  }

  @Nested
  @DisplayName("/oasys/assessment/{oasysPK}/sign")
  inner class Sign {
    private lateinit var assessment: Assessment
    private lateinit var oasysAssessment: OasysAssessment
    private val endpoint = { "/oasys/assessment/${oasysAssessment.oasysAssessmentPk}/sign" }

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
          answers = mapOf("assessment_complete" to Answer(value = "YES")),
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
      val request = """
        {
          "counterSignType": "SELF"
        }
      """.trimIndent()

      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .bodyValue(request)
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_OASYS' is not present on the JWT`() {
      val request = """
        {
          "counterSignType": "SELF",
          "oasysUserID": "123",
          "oasysUserName": "John Doe"
        }
      """.trimIndent()

      webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .bodyValue(request)
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it updates and returns the version of the assessment as Signed`() {
      val request = """
        {
          "counterSignType": "SELF",
          "oasysUserID": "123",
          "oasysUserName": "John Doe"
        }
      """.trimIndent()

      val response = webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(OasysAssessmentResponse::class.java)
        .returnResult()
        .responseBody

      val updatedAssessment = assessmentRepository.findByUuid(assessment.uuid)

      assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)
      val initialVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNSIGNED }
      val unvalidatedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNVALIDATED }
      val signedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.SELF_SIGNED }

      assertThat(initialVersion).isNull()
      assertThat(unvalidatedVersion).isNotNull
      assertThat(signedVersion).isNotNull

      assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentVersion).isEqualTo(signedVersion?.versionNumber).isEqualTo(1)
      assertThat(response?.sentencePlanId).isNull()
      assertThat(response?.sentencePlanVersion).isNull()
    }

    @Test
    fun `it returns Conflict when the assessment is already signed`() {
      assessment.assessmentVersions = assessment.assessmentVersions + AssessmentVersion(
        assessment = assessment,
        answers = mapOf("assessment_complete" to Answer(value = "YES")),
        createdAt = LocalDateTime.now().minusHours(1),
        tag = Tag.SELF_SIGNED,
        versionNumber = 2,
      )
      assessmentRepository.save(assessment)

      val request = """
        {
          "counterSignType": "SELF",
          "oasysUserID": "123",
          "oasysUserName": "John Doe"
        }
      """.trimIndent()

      val response = webTestClient.post().uri(endpoint())
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
        .bodyValue(request)
        .exchange()
        .expectStatus().isEqualTo(409)
        .expectBody(ErrorResponse::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.developerMessage).isEqualTo("The current assessment version is already SELF_SIGNED.")
    }
  }

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

      assertThat(updatedAssessment!!.assessmentVersions.count()).isEqualTo(2)
      val initialVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNSIGNED }
      val unvalidatedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.UNVALIDATED }
      val lockedVersion = updatedAssessment.assessmentVersions.find { it.tag == Tag.LOCKED_INCOMPLETE }

      assertThat(initialVersion).isNull()
      assertThat(unvalidatedVersion).isNotNull
      assertThat(lockedVersion).isNotNull

      assertThat(response?.sanAssessmentId).isEqualTo(assessment.uuid)
      assertThat(response?.sanAssessmentVersion).isEqualTo(lockedVersion?.versionNumber).isEqualTo(1)
      assertThat(response?.sentencePlanId).isNull()
      assertThat(response?.sentencePlanVersion).isNull()
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
