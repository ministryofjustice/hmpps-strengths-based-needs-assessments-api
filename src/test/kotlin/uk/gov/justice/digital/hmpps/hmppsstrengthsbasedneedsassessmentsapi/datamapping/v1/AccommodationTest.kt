package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AccommodationTest {
  private val sut = Accommodation()

  @Test
  fun q3() {
    val scenarios = listOf<Pair<Answers, String>>(
      Pair(emptyMap(), ""),
    ) + mapOf(
      null to "",
      Value.TEMPORARY to "YES",
      Value.NO_ACCOMMODATION to "YES",
      Value.SETTLED to "NO",
    ).map { Pair(mapOf(Field.CURRENT_ACCOMMODATION.lower to Answer(value = it.key?.name)), it.value) }

    for ((answers, expected) in scenarios) {
      val answersProvider = AnswersProvider(answers, testFormConfig)
      val result = sut.map(answersProvider)

      assertContains(result, "o3-3")
      assertEquals(expected, result["o3-3"])
    }
  }

  @Test
  fun q4() {
    val scenarios = listOf(
      Pair(emptyMap(), ""),
      Pair(mapOf(Field.CURRENT_ACCOMMODATION.lower to Answer(value = Value.NO_ACCOMMODATION.name)), "2"),
    ) + mapOf(
      null to "",
      Value.YES to "0",
      Value.YES_WITH_CONCERNS to "1",
      Value.NO to "2",
    ).map { Pair(mapOf(Field.SUITABLE_HOUSING.lower to Answer(value = it.key?.name)), it.value) }

    for ((answers, expected) in scenarios) {
      val answersProvider = AnswersProvider(answers, testFormConfig)
      val result = sut.map(answersProvider)

      assertContains(result, "o3-4")
      assertEquals(expected, result["o3-4"])
    }
  }

  @Test
  fun q98() {
    val scenarios = listOf<Pair<Answers, String>>(
      Pair(emptyMap(), ""),
    ) + mapOf(
      null to "",
      "" to "",
      "test" to "test",
    ).map { Pair(mapOf(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM.lower to Answer(value = it.key)), it.value) }

    for ((answers, expected) in scenarios) {
      val answersProvider = AnswersProvider(answers, testFormConfig)
      val result = sut.map(answersProvider)

      assertContains(result, "o3-98")
      assertEquals(expected, result["o3-98"])
    }
  }

  @Test
  fun q99() {
    val scenarios = listOf<Pair<Answers, String>>(
      Pair(emptyMap(), ""),
    ) + mapOf(
      null to "",
      "" to "",
      "test" to "test",
    ).map { Pair(mapOf(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING.lower to Answer(value = it.key)), it.value) }

    for ((answers, expected) in scenarios) {
      val answersProvider = AnswersProvider(answers, testFormConfig)
      val result = sut.map(answersProvider)

      assertContains(result, "o3-99")
      assertEquals(expected, result["o3-99"])
    }
  }
}
