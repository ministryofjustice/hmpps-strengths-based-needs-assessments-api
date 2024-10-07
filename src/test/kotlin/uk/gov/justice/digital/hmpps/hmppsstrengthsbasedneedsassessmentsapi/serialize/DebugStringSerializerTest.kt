package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.serialize

import com.fasterxml.jackson.core.JsonGenerator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Nested
import kotlin.test.BeforeTest
import kotlin.test.Test

class DebugStringSerializerTest {
  private val mockJsonGenerator: JsonGenerator = mockk()
  private val sut = DebugStringSerializer()

  @BeforeTest
  fun setUp() {
    clearAllMocks()
    every { mockJsonGenerator.writeString(any<String>()) } returns Unit
  }

  @Nested
  inner class Serialize {
    @Test
    fun `serializes debug message when Debug is enabled`() {
      mockkObject(DebugStringSerializerConfig)
      every { DebugStringSerializerConfig.Companion.isDebugEnabled() } returns true

      sut.serialize("test debug message", mockJsonGenerator, null)

      verify(exactly = 1) {
        mockJsonGenerator.writeString("test debug message")
      }
    }

    @Test
    fun `hides debug message when Debug is disabled`() {
      mockkObject(DebugStringSerializerConfig)
      every { DebugStringSerializerConfig.Companion.isDebugEnabled() } returns false

      sut.serialize("test debug message", mockJsonGenerator, null)

      verify(exactly = 1) {
        mockJsonGenerator.writeString("Check the logs or enable Debug mode for more information.")
      }
    }
  }
}
