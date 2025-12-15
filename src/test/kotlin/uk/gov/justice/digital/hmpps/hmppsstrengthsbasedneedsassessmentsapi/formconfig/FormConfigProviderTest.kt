package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config.ApplicationConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.exception.FormConfigNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import java.net.http.HttpClient
import java.net.http.HttpResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormConfigProviderTest: IntegrationTest() {
  private val mockHttpClient: HttpClient = mockk()
  private val appConfig: ApplicationConfig = ApplicationConfig(
    formConfigBaseUrl = "http://test-url/config",
    activeProfiles = "",
  )

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  private lateinit var sut: FormConfigProvider

  @BeforeTest
  fun setUp() {
    clearAllMocks()
    sut = FormConfigProvider(appConfig, mockHttpClient, objectMapper)
  }

  @Nested
  inner class Get {
    @Test
    fun `throws exception when form config not found`() {
      val mockResponse: HttpResponse<String> = mockk()
      every { mockResponse.statusCode() } returns 404

      every { mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

      val exception = assertFailsWith<FormConfigNotFoundException>(
        block = {
          sut.get(AssessmentFormInfo(formVersion = "1.0"))
        },
      )
      assertEquals("Unable to fetch form config from http://test-url/config/1/0", exception.message)
    }

    @Test
    fun `returns form config`() {
      val mockResponse: HttpResponse<String> = mockk()
      every { mockResponse.statusCode() } returns 200
      every { mockResponse.body() } returns
        """
        {
          "version": "1.1",
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
        FormConfig(
          "1.1",
          mapOf("test-field" to Field("test-field", listOf(Option("val-1")))),
        ),
        sut.get(AssessmentFormInfo(formVersion = "1.1")),
      )

      verify(exactly = 1) {
        mockHttpClient.send(
          withArg { assertEquals("http://test-url/config/1/1", it.uri().toString()) },
          any<HttpResponse.BodyHandler<String>>(),
        )
      }
    }
  }

  @Nested
  inner class GetLatest {
    @Test
    fun `throws exception when form config not found`() {
      val mockResponse: HttpResponse<String> = mockk()
      every { mockResponse.statusCode() } returns 404

      every { mockHttpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockResponse

      val exception = assertFailsWith<FormConfigNotFoundException>(
        block = {
          sut.getLatest()
        },
      )
      assertEquals("Unable to fetch form config from http://test-url/config/latest", exception.message)
    }

    @Test
    fun `returns form config`() {
      val mockResponse: HttpResponse<String> = mockk()
      every { mockResponse.statusCode() } returns 200
      every { mockResponse.body() } returns
        """
        {
          "version": "1.1",
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
        FormConfig(
          "1.1",
          mapOf("test-field" to Field("test-field", listOf(Option("val-1")))),
        ),
        sut.getLatest(),
      )

      verify(exactly = 1) {
        mockHttpClient.send(
          withArg { assertEquals("http://test-url/config/latest", it.uri().toString()) },
          any<HttpResponse.BodyHandler<String>>(),
        )
      }
    }
  }
}
