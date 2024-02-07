package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest

@AutoConfigureWebTestClient(timeout = "6000000")
@DisplayName("AssessmentController")
class AssessmentControllerTest : IntegrationTest() {
  @Nested
  @DisplayName("/assessment/create")
  inner class CreateAssessment {
    @Test
    fun `it returns Unauthorized when there is no JWT`() {
      webTestClient.post().uri("/assessment/create")
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `it returns Forbidden when the role 'STRENGTHS_AND_NEEDS_WRITE' is not present on the JWT`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
      )

      webTestClient.post().uri("/assessment/create")
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

      webTestClient.post().uri("/assessment/create")
        .bodyValue(request)
        .header(HttpHeaders.CONTENT_TYPE, "application/json")
        .headers(setAuthorisation(roles = listOf("ROLE_STRENGTHS_AND_NEEDS_WRITE")))
        .exchange()
        .expectStatus().isOk
    }
  }
}
