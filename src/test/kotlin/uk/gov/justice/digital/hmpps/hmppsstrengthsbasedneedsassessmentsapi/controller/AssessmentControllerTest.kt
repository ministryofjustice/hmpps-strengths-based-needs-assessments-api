package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.AssessmentResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:db/test/assessment/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
  ),
)
@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController")
class AssessmentControllerTest : IntegrationTest() {
  @Nested
  @DisplayName("/assessment/create")
  inner class CreateAssessment {
    private val endpoint = "/assessment/create"

    @Test
    fun `it returns Unauthorized when there is no JWT`() {
      webTestClient.post().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'STRENGTHS_AND_NEEDS_WRITE' is not present on the JWT`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
      )

      webTestClient.post().uri(endpoint)
        .bodyValue(request)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isForbidden
    }

    @Test
    fun `it returns the created assessment`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
      )

      webTestClient.post().uri(endpoint)
        .bodyValue(request)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .exchange()
        .expectStatus().isOk
    }
  }

  @Nested
  @DisplayName("/assessment/{assessmentUuid}/version/{tag}/answers")
  inner class GetAnswers {
    private val assessmentUuid = "7507b51e-a6e1-4820-9298-84c717cbacfc"
    private val endpoint = "/assessment/$assessmentUuid/version/validated/answers"

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
    fun `it returns Not Found when no answers exist for the given assessment UUID`() {
      webTestClient.get().uri("/assessment/00000000-0000-0000-0000-000000000000/version/validated/answers")
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `it returns Not Found when no answers exist for the given tag`() {
      webTestClient.get().uri("/assessment/$assessmentUuid/version/foo/answers")
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `it returns the latest answers for a given assessment and tag`() {
      val response = webTestClient.get().uri(endpoint)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isOk
        .expectBody(object : ParameterizedTypeReference<Map<String, Answer>>() {})
        .returnResult()
        .responseBody

      assertThat(response?.get("current_accommodation")).isNotNull
      assertThat(response?.get("current_accommodation")?.value).isEqualTo("SETTLED")
    }
  }

  @Nested
  @DisplayName("/assessment/{assessmentUuid}")
  inner class GetAssessment {
    private val assessmentUuid = "7507b51e-a6e1-4820-9298-84c717cbacfc"
    private val endpoint = "/assessment/$assessmentUuid"

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

      assertThat(response?.metaData?.uuid).isEqualTo(UUID.fromString(assessmentUuid))
      assertThat(response?.metaData?.createdAt).isEqualTo(LocalDateTime.parse("2024-01-01T12:00:00.000"))
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf("0000000001", "0000000002"))
      assertThat(response?.metaData?.versionTag).isEqualTo("unvalidated")
      assertThat(response?.metaData?.versionCreatedAt).isEqualTo(LocalDateTime.parse("2024-01-10T12:00:00.000"))
      assertThat(response?.metaData?.versionUuid).isEqualTo(UUID.fromString("a6b76ab6-3caf-4298-a3eb-5e033a0cf379"))
      assertThat(response?.metaData?.formVersion).isEqualTo("1.0")

      assertThat(response?.assessment?.get("current_accommodation")?.value).isEqualTo("SETTLED")

      assertThat(response?.oasysEquivalent?.get("foo")).isEqualTo("BAR")
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

      assertThat(response?.metaData?.uuid).isEqualTo(UUID.fromString(assessmentUuid))
      assertThat(response?.metaData?.createdAt).isEqualTo(LocalDateTime.parse("2024-01-01T12:00:00.000"))
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf("0000000001", "0000000002"))
      assertThat(response?.metaData?.versionTag).isEqualTo("validated")
      assertThat(response?.metaData?.versionCreatedAt).isEqualTo(LocalDateTime.parse("2024-01-10T12:00:00.000"))
      assertThat(response?.metaData?.versionUuid).isEqualTo(UUID.fromString("d9e9d8ff-584e-46a2-a698-d13ba1295a62"))
      assertThat(response?.metaData?.formVersion).isEqualTo("1.0")

      assertThat(response?.assessment?.get("current_accommodation")?.value).isEqualTo("SETTLED")

      assertThat(response?.oasysEquivalent?.get("foo")).isEqualTo("BAR")
    }

    @Test
    fun `it returns an assessment for an assessment UUID and before a given date`() {
      val response = webTestClient.get()
        .uri(
          UriComponentsBuilder
            .fromPath(endpoint)
            .queryParam("until", LocalDateTime.parse("2024-01-05T00:00:00.000").toEpochSecond(ZoneOffset.UTC))
            .build().toUriString(),
        )
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_READ")))
        .exchange()
        .expectStatus().isOk
        .expectBody(AssessmentResponse::class.java)
        .returnResult()
        .responseBody

      assertThat(response?.metaData?.uuid).isEqualTo(UUID.fromString(assessmentUuid))
      assertThat(response?.metaData?.createdAt).isEqualTo(LocalDateTime.parse("2024-01-01T12:00:00.000"))
      assertThat(response?.metaData?.oasys_pks).isEqualTo(listOf("0000000001", "0000000002"))
      assertThat(response?.metaData?.versionTag).isEqualTo("validated")
      assertThat(response?.metaData?.versionCreatedAt).isEqualTo(LocalDateTime.parse("2024-01-01T12:00:00.000"))
      assertThat(response?.metaData?.versionUuid).isEqualTo(UUID.fromString("6e1d9bc7-7df9-425a-aaf4-35cb699a7bf2"))
      assertThat(response?.metaData?.formVersion).isEqualTo("1.0")

      assertThat(response?.assessment?.get("current_accommodation")?.value).isEqualTo("NO_ACCOMMODATION")

      assertThat(response?.oasysEquivalent?.get("foo")).isEqualTo("BAR")
    }
  }
}
