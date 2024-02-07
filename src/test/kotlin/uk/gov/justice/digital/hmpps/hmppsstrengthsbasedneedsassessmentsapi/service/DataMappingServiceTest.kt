package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.MappingProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.FormVersionNotFoundException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Field as FormField

class DataMappingServiceTest {
  private val mockFormConfigProvider: FormConfigProvider = mockk()
  private val mockMappingProvider: MappingProvider = mockk()
  private val mockSectionMapping: SectionMapping = mockk()

  @BeforeTest
  fun setUp() {
    clearAllMocks()
    val testConfig = FormConfig("1.0", mapOf(Field.TEST_FIELD.lower to FormField(Field.TEST_FIELD.lower)))
    every { mockFormConfigProvider.get("1.0") } returns testConfig
  }

  @Test
  fun `getOasysEquivalent throws exception when assessment does not have a form version`() {
    val sut = DataMappingService(mockFormConfigProvider, mockMappingProvider)

    listOf(null, Assessment()).forEach {
      val exception = assertFailsWith<FormVersionNotFoundException>(
        block = {
          sut.getOasysEquivalent(AssessmentVersion(id = 123, assessment = it))
        },
      )
      assertEquals("No form version found for assessment ID 123", exception.message)
    }
  }

  @Test
  fun `getOasysEquivalent returns empty result`() {
    every { mockSectionMapping.map(any<AnswersProvider>()) } returns emptyMap()
    every { mockMappingProvider.get("1.0") } returns setOf(mockSectionMapping)

    val sut = DataMappingService(mockFormConfigProvider, mockMappingProvider)
    val assessment = AssessmentVersion(assessment = Assessment(info = AssessmentFormInfo(formVersion = "1.0")))
    val result = sut.getOasysEquivalent(assessment)

    assertEquals(emptyMap(), result)
  }

  @Test
  fun `getOasysEquivalent returns non-empty result comprising of multiple section mappings`() {
    val mockSectionMappingTwo: SectionMapping = mockk()

    every { mockSectionMapping.map(any<AnswersProvider>()) } returns mapOf("oasys-key-1" to "val-1")
    every { mockSectionMappingTwo.map(any<AnswersProvider>()) } returns mapOf("oasys-key-2" to listOf("val-2", "val-3"))
    every { mockMappingProvider.get("1.0") } returns setOf(mockSectionMapping, mockSectionMappingTwo)

    val sut = DataMappingService(mockFormConfigProvider, mockMappingProvider)

    val assessment = AssessmentVersion(
      assessment = Assessment(info = AssessmentFormInfo(formVersion = "1.0")),
      answers = mapOf(Field.TEST_FIELD.lower to Answer(value = "all good")),
    )

    val result = sut.getOasysEquivalent(assessment)

    verify(exactly = 1) {
      mockSectionMapping.map(withArg { assertEquals("all good", it.answer(Field.TEST_FIELD).value) })
      mockSectionMappingTwo.map(withArg { assertEquals("all good", it.answer(Field.TEST_FIELD).value) })
    }

    assertEquals(
      mapOf(
        "oasys-key-1" to "val-1",
        "oasys-key-2" to listOf("val-2", "val-3"),
      ),
      result,
    )
  }
}
