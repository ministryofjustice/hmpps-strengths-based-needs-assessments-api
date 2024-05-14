package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.HmppsStrengthsBasedNeedsAssessmentsApi
import java.time.Duration

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@ContextConfiguration
@SpringBootTest(
  classes = [HmppsStrengthsBasedNeedsAssessmentsApi::class],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ActiveProfiles(profiles = ["test"])
@Tag("integration")
abstract class IntegrationTest {
  var session: MockHttpSession? = null
  var request: MockHttpServletRequest? = null

  @Autowired
  internal lateinit var webTestClient: WebTestClient

  @Autowired
  internal lateinit var jwtHelper: JwtAuthHelper

  @BeforeEach
  fun beforeEach() {
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
    session?.run { clearAttributes() }
    session = null
  }

  private fun startRequest() {
    request = MockHttpServletRequest()
    request?.let {
      it.session = session
      RequestContextHolder.setRequestAttributes(ServletRequestAttributes(it))
    }
  }

  private fun endRequest() {
    (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).requestCompleted()
    RequestContextHolder.resetRequestAttributes()
    request = null
  }

  internal fun setAuthorisation(
    user: String = "test-api-client",
    fullName: String = "Test Client",
    scope: List<String> = listOf("read", "write"),
    roles: List<String> = emptyList(),
  ): (HttpHeaders) -> Unit {
    val jwt = jwtHelper.createJwt(
      subject = user,
      fullName = fullName,
      scope = scope,
      expiryTime = Duration.ofHours(1L),
      roles = roles,
    )

    return { it.set(HttpHeaders.AUTHORIZATION, "Bearer $jwt") }
  }
}
