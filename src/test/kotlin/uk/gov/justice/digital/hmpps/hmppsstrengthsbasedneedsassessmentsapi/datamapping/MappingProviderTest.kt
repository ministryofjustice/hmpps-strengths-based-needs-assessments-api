package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception.MappingNotFoundException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MappingProviderTest {
  private lateinit var sut: MappingProvider

  @BeforeTest
  fun setUp() {
    sut = MappingProvider()
  }

  @ParameterizedTest(name = "should throw an exception for version `{0}`")
  @NullSource
  @ValueSource(strings = ["X.Y", ""])
  fun `throws exception when version not found`(version: String?) {
    assertFailsWith<MappingNotFoundException>(
      block = {
        sut.get(version)
      },
    )
  }

  @Test
  fun `returns section mappings for existing form version`() {
    val result = sut.get("1.0")
    assertIs<Set<SectionMapping>>(result)
    assertTrue(result.isNotEmpty())
  }
}
