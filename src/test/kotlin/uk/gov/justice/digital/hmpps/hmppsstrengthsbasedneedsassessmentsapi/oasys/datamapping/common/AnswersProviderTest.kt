package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FieldType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Option
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.exception.InvalidMappingException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Field as FormConfigField

class AnswersProviderTest {
  private val testAnswers = mapOf(
    Field.CURRENT_ACCOMMODATION.lower to Answer(value = Value.SETTLED.name),
    Field.FINANCE_INCOME.lower to Answer(values = listOf(Value.FAMILY_OR_FRIENDS.name)),
    Field.EDUCATION_DIFFICULTIES.lower to Answer(values = listOf("")),
    Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION.lower to Answer(collection = listOf()),
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
        Field.FINANCE_INCOME.lower to FormConfigField(
          Field.FINANCE_INCOME.lower,
          listOf(Option(Value.FAMILY_OR_FRIENDS.name)),
          FieldType.CHECKBOX,
        ),
        Field.EDUCATION_DIFFICULTIES.lower to FormConfigField(
          Field.EDUCATION_DIFFICULTIES.lower,
          listOf(Option("Some value")),
          FieldType.CHECKBOX,
        ),
        Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION.lower to FormConfigField(
          Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION.lower,
          emptyList(),
          FieldType.COLLECTION,
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
      val answer = sut.answer(Field.CURRENT_ACCOMMODATION)

      assertEquals(Value.SETTLED.name, answer.value)

      val valuesException = assertFailsWith<InvalidMappingException>(
        block = { answer.values },
      )
      assertEquals("Invalid use of '.values' on a ${SingleValueAnswer::class.simpleName}", valuesException.message)

      val collectionException = assertFailsWith<InvalidMappingException>(
        block = { answer.collection },
      )
      assertEquals("Invalid use of '.collection' on a ${SingleValueAnswer::class.simpleName}", collectionException.message)
    }

    @Test
    fun `returns the checkbox values of an existing answer`() {
      val answer = sut.answer(Field.FINANCE_INCOME)

      assertEquals(listOf(Value.FAMILY_OR_FRIENDS.name), answer.values)

      val valueException = assertFailsWith<InvalidMappingException>(
        block = { answer.value },
      )
      assertEquals("Invalid use of '.value' on a ${MultipleValuesAnswer::class.simpleName}", valueException.message)

      val collectionException = assertFailsWith<InvalidMappingException>(
        block = { answer.collection },
      )
      assertEquals("Invalid use of '.collection' on a ${MultipleValuesAnswer::class.simpleName}", collectionException.message)
    }

    @Test
    fun `returns empty checkbox values when the answer is an array of empty string`() {
      val answer = sut.answer(Field.EDUCATION_DIFFICULTIES)
      assertEquals(emptyList(), answer.values)
    }

    @Test
    fun `returns the collection values of an existing answer`() {
      val answer = sut.answer(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION)

      assertEquals(emptyList(), answer.collection)

      val valueException = assertFailsWith<InvalidMappingException>(
        block = { answer.value },
      )
      assertEquals("Invalid use of '.value' on a ${CollectionAnswer::class.simpleName}", valueException.message)

      val valuesException = assertFailsWith<InvalidMappingException>(
        block = { answer.values },
      )
      assertEquals("Invalid use of '.values' on a ${CollectionAnswer::class.simpleName}", valuesException.message)
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
      sut.answer(Field.FINANCE_INCOME)
      val exception = assertFailsWith<InvalidMappingException>(
        block = {
          sut.get(Value.NO_ACCOMMODATION)
        },
      )
      assertEquals(
        "NO_ACCOMMODATION is not a valid option for field finance_income in form config version 1.0",
        exception.message,
      )
    }

    @Test
    fun `returns value name for a valid field value`() {
      sut.answer(Field.FINANCE_INCOME)
      assertEquals("FAMILY_OR_FRIENDS", sut.get(Value.FAMILY_OR_FRIENDS))
    }
  }
}
