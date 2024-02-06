package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.mockito.kotlin.any
import org.mockito.kotlin.argForWhich
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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
  private val mockFormConfigProvider: FormConfigProvider = mock()
  private val mockMappingProvider: MappingProvider = mock()
  private val mockSectionMapping: SectionMapping = mock()

  @BeforeTest
  fun setUp() {
    reset(
      mockFormConfigProvider,
      mockMappingProvider,
      mockSectionMapping,
    )
    whenever(mockFormConfigProvider.get("1.0")).thenReturn(
      FormConfig("1.0", mapOf(Field.TEST_FIELD.lower to FormField(Field.TEST_FIELD.lower))),
    )
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
    whenever(mockSectionMapping.map(any<AnswersProvider>())).thenReturn(emptyMap())
    whenever(mockMappingProvider.get("1.0")).thenReturn(setOf(mockSectionMapping))

    val sut = DataMappingService(mockFormConfigProvider, mockMappingProvider)
    val assessment = AssessmentVersion(assessment = Assessment(info = AssessmentFormInfo(formVersion = "1.0")))
    val result = sut.getOasysEquivalent(assessment)

    assertEquals(emptyMap(), result)
  }

  @Test
  fun `getOasysEquivalent returns non-empty result comprising of multiple section mappings`() {
    val mockSectionMappingTwo: SectionMapping = mock()

    whenever(mockSectionMapping.map(any<AnswersProvider>())).thenReturn(mapOf("oasys-key-1" to "val-1"))
    whenever(mockSectionMappingTwo.map(any<AnswersProvider>())).thenReturn(mapOf("oasys-key-2" to listOf("val-2", "val-3")))
    whenever(mockMappingProvider.get("1.0")).thenReturn(setOf(mockSectionMapping, mockSectionMappingTwo))

    val sut = DataMappingService(mockFormConfigProvider, mockMappingProvider)

    val assessment = AssessmentVersion(
      assessment = Assessment(info = AssessmentFormInfo(formVersion = "1.0")),
      answers = mapOf(Field.TEST_FIELD.lower to Answer(value = "all good")),
    )

    val result = sut.getOasysEquivalent(assessment)

    verify(mockSectionMapping, times(1)).map(argForWhich { answer(Field.TEST_FIELD).value == "all good" })
    verify(mockSectionMappingTwo, times(1)).map(argForWhich { answer(Field.TEST_FIELD).value == "all good" })

    assertEquals(
      mapOf(
        "oasys-key-1" to "val-1",
        "oasys-key-2" to listOf("val-2", "val-3"),
      ),
      result,
    )
  }
}
