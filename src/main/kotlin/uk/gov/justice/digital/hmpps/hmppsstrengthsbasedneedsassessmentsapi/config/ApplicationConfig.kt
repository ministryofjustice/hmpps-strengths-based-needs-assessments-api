package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig(
  @Value("\${app.link.base-url}")
  val baseUrl: String,

  @Value("\${app.session.max-age}")
  val sessionMaxAge: Int,
)
