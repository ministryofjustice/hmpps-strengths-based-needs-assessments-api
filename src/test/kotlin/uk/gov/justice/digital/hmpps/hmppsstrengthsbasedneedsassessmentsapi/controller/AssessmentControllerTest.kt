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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest

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
    val endpoint = "/assessment/create"

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
    val assessmentUuid = "7507b51e-a6e1-4820-9298-84c717cbacfc"
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
}
