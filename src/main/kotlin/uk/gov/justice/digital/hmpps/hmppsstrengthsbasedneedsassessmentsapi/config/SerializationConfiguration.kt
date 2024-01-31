package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class SerializationConfiguration {
  @Bean
  @OptIn(ExperimentalSerializationApi::class)
  fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
    return KotlinSerializationJsonHttpMessageConverter(
      Json {
        ignoreUnknownKeys = true
        explicitNulls = false
      },
    )
  }
}
