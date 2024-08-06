package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationConfigTest {
  @Nested
  inner class IsDebugEnabled {
    @ParameterizedTest
    @ValueSource(strings = ["dev", "local", "postgres,dev"])
    fun `Debug mode is enabled for active profiles '{0}'`(profiles: String) {
      val appConfig = ApplicationConfig(
        formConfigBaseUrl = "",
        activeProfiles = profiles,
      )
      assertTrue { appConfig.isDebugEnabled() }
    }

    @ParameterizedTest
    @ValueSource(strings = ["prod", "production", ""])
    fun `Debug mode is disabled for active profiles '{0}'`(profiles: String) {
      val appConfig = ApplicationConfig(
        formConfigBaseUrl = "",
        activeProfiles = profiles,
      )
      assertFalse { appConfig.isDebugEnabled() }
    }
  }
}
