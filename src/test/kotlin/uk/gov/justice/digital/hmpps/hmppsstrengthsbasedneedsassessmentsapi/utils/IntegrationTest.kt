package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.TelemetryService
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("postgres", "test")
@Tag("integration")
abstract class IntegrationTest {
  private lateinit var session: MockHttpSession
  private lateinit var request: MockHttpServletRequest
  protected lateinit var webTestClient: WebTestClient

  @LocalServerPort
  private var port: Int = 0

  @Autowired
  internal lateinit var jwtAuthHelper: JwtAuthorisationHelper

  @MockkBean
  lateinit var telemetryService: TelemetryService

  @BeforeEach
  fun beforeEach() {
    webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://localhost:$port")
      .build()

    startSession()
    startRequest()
  }

  @AfterEach
  fun afterEach() {
    endRequest()
    endSession()
  }

  private fun startSession() {
    session = MockHttpSession()
  }

  private fun endSession() {
    session.clearAttributes()
  }

  private fun startRequest() {
    request = MockHttpServletRequest()
    request.setSession(session)
    RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
  }

  private fun endRequest() {
    (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).requestCompleted()
    RequestContextHolder.resetRequestAttributes()
    request.clearAttributes()
  }

  internal fun setAuthorisation(
    username: String? = "AUTH_ADM",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf("read"),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisationHeader(username = username, scope = scopes, roles = roles)
}
