package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class ApplicationConfig(
  @Value("\${app.form-config.base-url}")
  val formConfigBaseUrl: String,
  @Value("\${spring.profiles.active:}")
  val activeProfiles: String,
) {
  @Bean
  fun httpClient(): HttpClient {
    return HttpClient.newBuilder().build()
  }

  fun isDebugEnabled(): Boolean = activeProfiles.split(",").any { listOf("local", "dev").contains(it) }
}
