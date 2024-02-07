package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.exception.FormConfigNotFoundException
import java.net.http.HttpClient
import java.net.http.HttpResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormConfigProviderTest {
  private val mockHttpClient: HttpClient = mockk()
  private val sut = FormConfigProvider(mockHttpClient, "http://test-url")

  @BeforeTest
  fun setUp() {
    clearAllMocks()
  }

  @Test
  fun `get throws exception when form config not found`() {
    val mockResponse: HttpResponse<String> = mockk()
    every { mockResponse.statusCode() } returns 404

    every { mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

    val exception = assertFailsWith<FormConfigNotFoundException>(
      block = {
        sut.get("1.0")
      },
    )
    assertEquals("Unable to fetch form config from http://test-url/sbna-poc/1/0/fields", exception.message)
  }

  @Test
  fun `get returns form config`() {
    val mockResponse: HttpResponse<String> = mockk()
    every { mockResponse.statusCode() } returns 200
    every { mockResponse.body() } returns
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
      """

    every { mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

    assertEquals(
      FormConfig("1.0", mapOf("test-field" to Field("test-field", listOf(Option("val-1"))))),
      sut.get("1.0"),
    )

    verify(exactly = 1) {
      mockHttpClient.send(
        withArg { assertEquals("http://test-url/sbna-poc/1/0/fields", it.uri().toString()) },
        any<HttpResponse.BodyHandler<String>>(),
      )
    }
  }
}
