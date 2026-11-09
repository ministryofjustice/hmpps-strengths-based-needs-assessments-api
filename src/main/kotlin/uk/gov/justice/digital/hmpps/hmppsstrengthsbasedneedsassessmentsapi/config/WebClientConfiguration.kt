package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.hmpps.kotlin.auth.authorisedWebClient
import uk.gov.justice.hmpps.kotlin.auth.healthWebClient
import java.time.Duration

@Configuration
class WebClientConfiguration(
  @param:Value("\${app.services.hmpps-auth.url}") val hmppsAuthBaseUri: String,
  @param:Value("\${api.health-timeout:2s}") val healthTimeout: Duration,
  @param:Value("\${api.timeout:20s}") val timeout: Duration,
  @param:Value("\${assessment-platform-api.base-url}") val assessmentPlatformBaseUrl: String,
  @param:Value("\${coordinator-api.base-url}") val coordinatorBaseURL: String,
) {
  @Bean
  fun webClientBuilder(): WebClient.Builder = WebClient.builder()

  // HMPPS Auth health ping is required if your service calls HMPPS Auth to get a token to call other services
  @Bean
  fun hmppsAuthHealthWebClient(builder: WebClient.Builder): WebClient = builder.healthWebClient(hmppsAuthBaseUri, healthTimeout)

  @Bean
  fun assessmentPlatformClient(
    authorizedClientManager: OAuth2AuthorizedClientManager,
    builder: WebClient.Builder,
  ): WebClient = builder.authorisedWebClient(
    authorizedClientManager.serialised(),
    registrationId = "assessment-platform-api",
    url = assessmentPlatformBaseUrl,
    timeout,
  )

  @Bean
  fun coordinatorClient(
    authorizedClientManager: OAuth2AuthorizedClientManager,
    builder: WebClient.Builder,
  ): WebClient = builder.authorisedWebClient(
    authorizedClientManager.serialised(),
    registrationId = "coordinator-api",
    url = coordinatorBaseURL,
    timeout,
  )

  // The default manager doesn't coordinate concurrent callers, so when a token is missing or expired every
  // in-flight request fetches its own - with the migrator's concurrency that's a burst of token requests
  // that HMPPS Auth starts resetting. Serialising means one caller fetches and the rest reuse the cached token.
  private fun OAuth2AuthorizedClientManager.serialised() = OAuth2AuthorizedClientManager { request ->
    synchronized(this) { authorize(request) }
  }
}
