package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class ThinkingBehaviours : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o11-2" to ::q2,
      "o11-3" to ::q3,
      "o11-4" to ::q4,
      "o11-6" to ::q6,
      "o11-7" to ::q7,
      "o11-9" to ::q9,
      "o11-11" to ::q11,
      "o11-12" to ::q12,
      "o11-97" to ::q97,
      "o11-98" to ::q98,
      "o11-99" to ::q99,
    )
  }

  private fun q2(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR).value) {
      ap.get(Value.NO) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.YES) -> "2"
      else -> null
    }
  }

  private fun q3(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR).value) {
      ap.get(Value.NO_VIOLENCE) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.YES_VIOLENCE) -> "2"
      else -> null
    }
  }

  private fun q4(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q6(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.LIMITED_PROBLEM_SOLVING) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q7(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q9(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q11(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION).value) {
      ap.get(Value.YES) -> "2"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "0"
      else -> null
    }
  }

  private fun q12(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST).value) {
      ap.get(Value.YES_OFFENCE_RELATED_SEXUAL_INTEREST) -> "2"
      ap.get(Value.SOME_OFFENCE_RELATED_SEXUAL_INTEREST) -> "1"
      ap.get(Value.NO_OFFENCE_RELATED_SEXUAL_INTEREST) -> "0"
      else -> null
    }
  }

  private fun q97(): Any? {
    return PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).notes()
  }

  private fun q98(): Any? {
    return PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfSeriousHarm()
  }

  private fun q99(): Any? {
    return PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfReoffending()
  }
}
