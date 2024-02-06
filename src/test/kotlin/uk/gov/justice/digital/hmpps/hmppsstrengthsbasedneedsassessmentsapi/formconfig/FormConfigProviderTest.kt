package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import org.mockito.kotlin.any
import org.mockito.kotlin.argForWhich
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.exception.FormConfigNotFoundException
import java.net.http.HttpClient
import java.net.http.HttpResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormConfigProviderTest {
  private val mockHttpClient: HttpClient = mock()
  private val sut = FormConfigProvider(mockHttpClient, "http://test-url")

  @BeforeTest
  fun setUp() {
    reset(mockHttpClient)
  }

  @Test
  fun `get throws exception when form config not found`() {
    val mockResponse: HttpResponse<String> = mock()
    whenever(mockResponse.statusCode()).thenReturn(404)

    whenever(mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenReturn(mockResponse)

    val exception = assertFailsWith<FormConfigNotFoundException>(
      block = {
        sut.get("1.0")
      },
    )
    assertEquals("Unable to fetch form config from http://test-url/sbna-poc/1/0/fields", exception.message)
  }

  @Test
  fun `get returns form config`() {
    val mockResponse: HttpResponse<String> = mock()
    whenever(mockResponse.statusCode()).thenReturn(200)
    whenever(mockResponse.body()).thenReturn(
      """
        {
          "version": "1.0",
          "fields": {
            "test-field": {
              "code": "test-field",
              "options": [
                {
                  "value": "val-1"
                }
              ]
            }
          }
        }
      """,
    )

    whenever(mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenReturn(mockResponse)

    assertEquals(
      FormConfig("1.0", mapOf("test-field" to Field("test-field", listOf(Option("val-1"))))),
      sut.get("1.0"),
    )

    verify(mockHttpClient, times(1)).send(
      argForWhich { uri().toString() == "http://test-url/sbna-poc/1/0/fields" },
      any<HttpResponse.BodyHandler<String>>(),
    )
  }
}
