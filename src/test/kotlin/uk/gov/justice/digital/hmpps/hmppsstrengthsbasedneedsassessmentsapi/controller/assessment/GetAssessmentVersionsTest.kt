package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.assessment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController: /assessment/{assessmentUuid}/all")
class GetAssessmentVersionsTest(
  @Autowired
  val assessmentRepository: AssessmentRepository,
) : IntegrationTest() {
  private lateinit var assessment: Assessment

  private val endpoint = { "/assessment/${assessment.uuid}/all" }
  private val now = LocalDateTime.now()

  @BeforeEach
  fun setup() {
    assessment = Assessment(uuid = UUID.randomUUID())
      .apply {
        assessmentVersions = listOf(
          AssessmentVersion(
            assessment = this,
            createdAt = now.minusDays(2),
            updatedAt = now.minusDays(2),
            tag = Tag.UNSIGNED,
            versionNumber = 0,
          ),
          AssessmentVersion(
            assessment = this,
            updatedAt = now.minusDays(1),
            createdAt = now.minusDays(1),
            tag = Tag.UNSIGNED,
            versionNumber = 1,
          ),
          AssessmentVersion(
            assessment = this,
            createdAt = now,
            updatedAt = now,
            tag = Tag.UNSIGNED,
            versionNumber = 2,
          ),
        )
        info = AssessmentFormInfo(formVersion = "1.0", assessment = this)
      }
    assessmentRepository.save(assessment)
  }

  @Test
  fun `it returns Unauthorized when there is no JWT`() {
    webTestClient.get().uri(endpoint())
      .exchange()
      .expectStatus().isUnauthorized
  }

  @Test
  fun `it returns Forbidden when the role 'ROLE_STRENGTHS_AND_NEEDS_READ' is not present on the JWT`() {
    webTestClient.get().uri(endpoint())
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isForbidden
  }

  @Test
  fun `it returns Not Found when no assessment exists for the given assessment UUID`() {
    webTestClient.get().uri("/assessment/00000000-0000-0000-0000-000000000000/all")
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isNotFound
  }

  @Test
  fun `it returns a list of Assessment versions for a given assessment UUID`() {
    val response = webTestClient.get().uri(endpoint())
      .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
      .exchange()
      .expectStatus().isOk
      .expectBodyList(AssessmentVersion::class.java)
      .returnResult()
      .responseBody

    assertThat(response?.map { it -> it.versionNumber }).isEqualTo(listOf(2, 1, 0))

    assertThat(response?.size).isEqualTo(3)
    (response?.find { it -> it.versionNumber == 0 }).let {
      println(response)
      assertThat(it).isNotNull
      assertThat(it?.tag).isEqualTo(Tag.UNSIGNED)
      assertThat(it?.createdAt).isEqualTo(now.minusDays(2))
      assertThat(it?.updatedAt).isEqualTo(now.minusDays(2))
    }

    (response?.find { it -> it.versionNumber == 1 }).let {
      assertThat(it).isNotNull
      assertThat(it?.tag).isEqualTo(Tag.UNSIGNED)
      assertThat(it?.updatedAt).isEqualTo(now.minusDays(1))
      assertThat(it?.createdAt).isEqualTo(now.minusDays(1))
    }
    (response?.find { it -> it.versionNumber == 2 }).let {
      assertThat(it).isNotNull
      assertThat(it?.tag).isEqualTo(Tag.UNSIGNED)
      assertThat(it?.updatedAt).isEqualTo(now)
      assertThat(it?.createdAt).isEqualTo(now)
    }
  }
}
