package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class LifestyleAssociates : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o7-2" to ::q2,
      "o7-3" to ::q3,
      "o7-4" to ::q4,
      "o7-5" to ::q5,
      "o7-97" to ::q97,
      "o7-98" to ::q98,
      "o7-99" to ::q99,
    )
  }

  private fun q2(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENDING_ACTIVITIES).value) {
      ap.get(Value.NO_OFFENDING_ACTIVITIES) -> "0"
      ap.get(Value.SOMETIMES_OFFENDING_ACTIVITIES) -> "1"
      ap.get(Value.YES_OFFENDING_ACTIVITIES) -> "2"
      else -> null
    }
  }

  private fun q3(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PEER_PRESSURE).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.SOME) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q4(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_MANIPULATIVE_PREDATORY_BEHAVIOUR).value) {
      ap.get(Value.YES) -> "2"
      ap.get(Value.SOME) -> "1"
      ap.get(Value.NO) -> "0"
      else -> null
    }
  }

  private fun q5(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_STABLE_BEHAVIOUR).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "2"
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
