package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.exception.FormConfigNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@JsonIgnoreProperties(ignoreUnknown = true)
data class FormConfig(
  val version: String,
  val fields: Map<String, Field> = emptyMap(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Field(
  val code: String,
  val options: List<Option> = emptyList(),
  val type: String = "",
  val section: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Option(
  val value: String? = null,
)

@Component
class FormConfigProvider(
  val appConfig: ApplicationConfig,
  val client: HttpClient,
  val decoder: ObjectMapper,
) {
  fun get(formInfo: AssessmentFormInfo): FormConfig {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("${appConfig.formConfigBaseUrl}/${formInfo.formVersion.replace(".", "/")}"))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200) {
      throw FormConfigNotFoundException("Unable to fetch form config from ${request.uri()}")
    }

    return decoder.readValue(response.body(), FormConfig::class.java)
  }

  fun getLatest(): FormConfig {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("${appConfig.formConfigBaseUrl}/latest"))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200) {
      throw FormConfigNotFoundException("Unable to fetch form config from ${request.uri()}")
    }

    return decoder.readValue(response.body(), FormConfig::class.java)
  }
}
