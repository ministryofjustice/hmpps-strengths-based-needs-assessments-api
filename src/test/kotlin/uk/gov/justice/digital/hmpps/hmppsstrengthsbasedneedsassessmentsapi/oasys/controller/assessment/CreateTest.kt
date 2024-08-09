package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.OasysPKGenerator
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("OasysAssessmentController: /oasys/assessment/create")
class CreateTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
  @Autowired
  val oasysAssessmentRepository: OasysAssessmentRepository,
) : IntegrationTest() {
  private val endpoint = "/oasys/assessment/create"
  private lateinit var assessment: Assessment
  private lateinit var oasysAss1: OasysAssessment
  private lateinit var oasysAss2: OasysAssessment

  @BeforeEach
  fun setUp() {
    assessment = Assessment()

    oasysAss1 = OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment)
    oasysAss2 = OasysAssessment(oasysAssessmentPk = OasysPKGenerator.new(), assessment = assessment)

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
    val request = """
          {
            "oasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "previousOasysAssessmentPk": "${oasysAss2.oasysAssessmentPk}",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation())
      .bodyValue(request)
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun `it returns Bad Request when the request body contains unknown parameters`() {
    val request = """
          {
            "oasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "previousOasysAssessmentPK": "${oasysAss2.oasysAssessmentPk}",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isBadRequest
  }

  @Test
  fun `it returns Conflict when the association already exists`() {
    val request = """
          {
            "oasysAssessmentPk": "${oasysAss2.oasysAssessmentPk}",
            "previousOasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isEqualTo(409)
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("OASys assessment with ID ${oasysAss2.oasysAssessmentPk} already exists.")
  }

  @Test
  fun `it returns Conflict when the association already exists and is soft deleted`() {
    oasysAss2.deleted = true
    oasysAssessmentRepository.save(oasysAss2)

    val request = """
          {
            "oasysAssessmentPk": "${oasysAss2.oasysAssessmentPk}",
            "previousOasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    val response = webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isEqualTo(409)
      .expectBody(ErrorResponse::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.userMessage).isEqualTo("OASys assessment with ID ${oasysAss2.oasysAssessmentPk} is soft deleted.")
  }

  @Test
  fun `it creates an assessment when only an OASys assessment PK provided and no assessment already exists`() {
    val newOasysPK = OasysPKGenerator.new()
    val regionPrisonCode = "test-prison-code"

    val request = """
          {
            "oasysAssessmentPk": "$newOasysPK",
            "regionPrisonCode": "$regionPrisonCode",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

    assertThat(newOasysAss).isNotNull
    assertThat(newOasysAss?.regionPrisonCode).isEqualTo(regionPrisonCode)
    assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk }).isEqualTo(
      listOf(
        newOasysPK,
      ),
    )
  }

  @Test
  fun `it associates a new OASys assessment to an existing assessment and creates a new Unsigned assessment version`() {
    val newOasysPK = OasysPKGenerator.new()
    val regionPrisonCode = "test-prison-code"

    val lockedVersion = assessment.assessmentVersions.first().apply { tag = Tag.LOCKED_INCOMPLETE }
    assessment.assessmentVersions = listOf(lockedVersion)
    assessmentRepository.save(assessment)

    val request = """
          {
            "oasysAssessmentPk": "$newOasysPK",
            "previousOasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "regionPrisonCode": "$regionPrisonCode",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

    assertThat(newOasysAss).isNotNull
    assertThat(newOasysAss?.regionPrisonCode).isEqualTo(regionPrisonCode)
    assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk })
      .containsExactlyInAnyOrder(newOasysPK, oasysAss1.oasysAssessmentPk, oasysAss2.oasysAssessmentPk)

    val assessmentVersions = newOasysAss?.assessment?.assessmentVersions
    assertNotNull(assessmentVersions)
    assertEquals(2, assessmentVersions.count())
    assertThat(mapOf(0 to Tag.LOCKED_INCOMPLETE, 1 to Tag.UNSIGNED))
      .containsExactlyInAnyOrderEntriesOf(assessmentVersions.associate { it.versionNumber to it.tag })
  }

  @Test
  fun `it associates a new OASys assessment to an existing assessment and returns the existing Unsigned assessment version`() {
    val newOasysPK = OasysPKGenerator.new()
    val regionPrisonCode = "test-prison-code"

    val request = """
          {
            "oasysAssessmentPk": "$newOasysPK",
            "previousOasysAssessmentPk": "${oasysAss1.oasysAssessmentPk}",
            "regionPrisonCode": "$regionPrisonCode",
            "userDetails": { "id": "user-id", "name": "John Doe" }
          }
    """.trimIndent()

    webTestClient.post().uri(endpoint)
      .header(HttpHeaders.CONTENT_TYPE, "application/json")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_OASYS")))
      .bodyValue(request)
      .exchange()
      .expectStatus().isOk

    val newOasysAss = oasysAssessmentRepository.findByOasysAssessmentPk(newOasysPK)

    assertThat(newOasysAss).isNotNull
    assertThat(newOasysAss?.regionPrisonCode).isEqualTo(regionPrisonCode)
    assertThat(newOasysAss?.assessment?.oasysAssessments?.map { it.oasysAssessmentPk })
      .containsExactlyInAnyOrder(newOasysPK, oasysAss1.oasysAssessmentPk, oasysAss2.oasysAssessmentPk)

    val assessmentVersions = newOasysAss?.assessment?.assessmentVersions
    assertNotNull(assessmentVersions)
    assertEquals(1, assessmentVersions.count())
    assertEquals(assessmentVersions.first().tag, Tag.UNSIGNED)
  }
}
