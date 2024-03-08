package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping

import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.exception.MappingNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MappingProviderTest {
  private lateinit var sut: MappingProvider

  @BeforeTest
  fun setUp() {
    sut = MappingProvider()
  }

  @Nested
  inner class Get {
    @ParameterizedTest(name = "should throw an exception for version `{0}`")
    @ValueSource(strings = ["X.Y", ""])
    fun `throws exception when version not found`(version: String) {
      val exception = assertFailsWith<MappingNotFoundException>(
        block = {
          sut.get(AssessmentFormInfo(formVersion = version))
        },
      )
      assertContains(exception.message!!, "No data mapping found for form version $version")
    }

    @Test
    fun `returns section mappings for existing form version`() {
      val result = sut.get(AssessmentFormInfo(formVersion = "1.0"))
      assertIs<Set<SectionMapping>>(result)
      assertTrue(result.isNotEmpty())
    }
  }
}
