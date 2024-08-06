package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Option
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.exception.InvalidMappingException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Field as FormConfigField

class AnswersProviderTest {
  private val testAnswers = mapOf(
    Field.CURRENT_ACCOMMODATION.lower to Answer(value = Value.SETTLED.name),
    Field.SUITABLE_HOUSING.lower to Answer(values = listOf(Value.YES.name)),
    Field.EDUCATION_DIFFICULTIES.lower to Answer(values = listOf("")),
  )

  private lateinit var sut: AnswersProvider

  @BeforeTest
  fun setUp() {
    val testFormConfig = FormConfig(
      "1.0",
      mapOf(
        Field.CURRENT_ACCOMMODATION.lower to FormConfigField(
          Field.CURRENT_ACCOMMODATION.lower,
          listOf(Option(Value.TEMPORARY.name), Option(Value.NO_ACCOMMODATION.name), Option(Value.SETTLED.name)),
        ),
        Field.SUITABLE_HOUSING.lower to FormConfigField(
          Field.SUITABLE_HOUSING.lower,
          listOf(Option(Value.YES.name), Option(Value.YES_WITH_CONCERNS.name), Option(Value.NO.name)),
        ),
        Field.EDUCATION_DIFFICULTIES.lower to FormConfigField(
          Field.EDUCATION_DIFFICULTIES.lower,
          listOf(Option("Some value")),
          "CHECKBOX",
        ),
      ),
    )

    sut = AnswersProvider(testAnswers, testFormConfig)
  }

  @Nested
  inner class Answer {
    @Test
    fun `throws exception when field not in config`() {
      val exception = assertFailsWith<InvalidMappingException>(
        block = {
          sut.answer(Field.TEST_FIELD)
        },
      )
      assertEquals("Field test_field does not exist in form config version 1.0", exception.message)
    }

    @Test
    fun `returns the value of an existing answer`() {
      var answer = sut.answer(Field.CURRENT_ACCOMMODATION)
      assertEquals(Value.SETTLED.name, answer.value)
      assertNull(answer.values)

      answer = sut.answer(Field.SUITABLE_HOUSING)
      assertEquals(listOf(Value.YES.name), answer.values)
      assertNull(answer.value)

      answer = sut.answer(Field.EDUCATION_DIFFICULTIES)
      assertEquals(emptyList(), answer.values)
      assertNull(answer.value)
    }
  }

  @Nested
  inner class Get {
    @Test
    fun `throws exception when called outside of a field context`() {
      val exception = assertFailsWith<InvalidMappingException>(
        block = {
          sut.get(Value.YES)
        },
      )
      assertEquals("Cannot obtain values without a field context. Call answer() first", exception.message)
    }

    @Test
    fun `throws exception for invalid field option`() {
      sut.answer(Field.SUITABLE_HOUSING)
      val exception = assertFailsWith<InvalidMappingException>(
        block = {
          sut.get(Value.NO_ACCOMMODATION)
        },
      )
      assertEquals(
        "NO_ACCOMMODATION is not a valid option for field suitable_housing in form config version 1.0",
        exception.message,
      )
    }

    @Test
    fun `returns value name for a valid field value`() {
      sut.answer(Field.SUITABLE_HOUSING)
      assertEquals("YES", sut.get(Value.YES))
    }
  }
}
