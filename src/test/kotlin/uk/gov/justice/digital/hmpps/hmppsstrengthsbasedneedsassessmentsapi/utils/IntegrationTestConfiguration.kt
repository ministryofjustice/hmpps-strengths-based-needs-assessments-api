package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils

import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.TelemetryService

@TestConfiguration
class IntegrationTestConfiguration {
  @Bean
  @Primary
  fun telemetryService(): TelemetryService = mockk()
}
