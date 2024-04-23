package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
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
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Option(
  val value: String? = null,
)

@Component
class FormConfigProvider(
  val client: HttpClient,
  @Value("\${app.form-config.base-url}")
  val formConfigBaseUrl: String,
  val decoder: ObjectMapper,
) {
  fun get(formInfo: AssessmentFormInfo): FormConfig {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("$formConfigBaseUrl/${formInfo.formName}/${formInfo.formVersion.replace(".", "/")}/fields"))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200) {
      throw FormConfigNotFoundException("Unable to fetch form config from ${request.uri()}")
    }

    return decoder.readValue(response.body(), FormConfig::class.java)
  }
}
