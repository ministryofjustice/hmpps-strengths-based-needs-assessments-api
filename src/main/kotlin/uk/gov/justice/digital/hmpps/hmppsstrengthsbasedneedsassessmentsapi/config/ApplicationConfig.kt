package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class ApplicationConfig(
  @Value("\${app.link.base-url}")
  val formBaseUrl: String,

  @Value("\${app.session.max-age}")
  val sessionMaxAge: Int,
) {
  @Bean
  fun httpClient(): HttpClient {
    return HttpClient.newBuilder().build()
  }
}
