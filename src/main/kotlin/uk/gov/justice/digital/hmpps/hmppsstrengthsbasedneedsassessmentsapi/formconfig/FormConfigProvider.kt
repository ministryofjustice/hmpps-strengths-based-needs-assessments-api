package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

class FormConfigProvider {
  fun get(version: String): FormConfig {
    val request = HttpRequest.newBuilder()
      .uri(URI.create("$formUrl/sbna-poc/${version.replace(".", "/")}/fields"))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    if (response.statusCode() != 200) {
      throw FormConfigNotFoundException("Unable to fetch form config from $request")
    }

    return decoder.decodeFromString(response.body())
  }

  companion object {
    private val client = HttpClient.newBuilder().build()
    private val formUrl = System.getenv("LINK_BASE_URL") ?: throw FormConfigNotFoundException("Form config URL not defined")
    private val decoder = Json { ignoreUnknownKeys = true }
  }
}
