package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.integration

import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("postgres", "test")
abstract class IntegrationTestBase {
  protected lateinit var webTestClient: WebTestClient

  @LocalServerPort
  private var port: Int = 0

  @BeforeEach
  fun beforeEach() {
    webTestClient = WebTestClient.bindToServer()
      .baseUrl("http://localhost:$port")
      .build()
  }
}
