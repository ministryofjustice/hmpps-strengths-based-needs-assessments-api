package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception.InvalidMappingException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.v1.testFormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AnswersProviderTest {
  private val testAnswers = mapOf(
    Field.CURRENT_ACCOMMODATION.lower to Answer(value = Value.SETTLED.name),
    Field.SUITABLE_HOUSING.lower to Answer(values = listOf(Value.YES.name)),
  )

  private lateinit var sut: AnswersProvider

  @BeforeTest
  fun setUp() {
    sut = AnswersProvider(testAnswers, testFormConfig)
  }

  @Test
  fun `answer throws exception when field not in config`() {
    val exception = assertFailsWith<InvalidMappingException>(
      block = {
        sut.answer(Field.TEST_FIELD)
      },
    )
    assertEquals("Field test_field does not exist in form config version 1.0", exception.message)
  }

  @Test
  fun `answer returns the value of an existing answer`() {
    var answer = sut.answer(Field.CURRENT_ACCOMMODATION)
    assertEquals(Value.SETTLED.name, answer.value)
    assertNull(answer.values)

    answer = sut.answer(Field.SUITABLE_HOUSING)
    assertEquals(listOf(Value.YES.name), answer.values)
    assertNull(answer.value)
  }

  @Test
  fun `get throws exception when called outside of a field context`() {
    val exception = assertFailsWith<InvalidMappingException>(
      block = {
        sut.get(Value.YES)
      },
    )
    assertEquals("Cannot obtain values without a field context. Call answer() first", exception.message)
  }

  @Test
  fun `get throws exception for invalid field option`() {
    sut.answer(Field.SUITABLE_HOUSING)
    val exception = assertFailsWith<InvalidMappingException>(
      block = {
        sut.get(Value.NO_ACCOMMODATION)
      },
    )
    assertEquals("NO_ACCOMMODATION is not a valid option for field suitable_housing in form config version 1.0", exception.message)
  }

  @Test
  fun `get returns value name for a valid field value`() {
    sut.answer(Field.SUITABLE_HOUSING)
    assertEquals("YES", sut.get(Value.YES))
  }
}
