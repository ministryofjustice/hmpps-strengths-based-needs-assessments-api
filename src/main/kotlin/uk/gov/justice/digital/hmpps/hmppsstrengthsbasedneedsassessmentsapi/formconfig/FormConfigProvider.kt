package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.exception.FormConfigNotFoundException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Serializable
data class FormConfig(
  val version: String,
  val fields: Map<String, Field> = emptyMap(),
)

@Serializable
data class Field(
  val code: String,
  val options: List<Option> = emptyList(),
)

@Serializable
data class Option(
  val value: String? = null,
)

@Component
class FormConfigProvider(
  val client: HttpClient,
  @Value("\${app.form.url}")
  val formUrl: String,
) {
  fun get(version: String): FormConfig {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("$formUrl/sbna-poc/${version.replace(".", "/")}/fields"))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200) {
      throw FormConfigNotFoundException("Unable to fetch form config from ${request.uri()}")
    }

    val decoder = Json { ignoreUnknownKeys = true }

    return decoder.decodeFromString(response.body())
  }
}
