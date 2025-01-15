package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Attitudes : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap = mapOf(
    "o12-1" to ::q1,
    "o12-3" to ::q3,
    "o12-4" to ::q4,
    "o12-9" to ::q9,
    "o12-97" to ::q97,
    "o12-98" to ::q98,
    "o12-99" to ::q99,
  )

  private fun q1(): Any? = when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR).value) {
    ap.get(Value.NO) -> "0"
    ap.get(Value.SOMETIMES) -> "1"
    ap.get(Value.YES) -> "2"
    else -> null
  }

  private fun q3(): Any? = when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE).value) {
    ap.get(Value.YES_POSITIVE) -> "0"
    ap.get(Value.NEGATIVE_ATTITUDE_NO_CONCERNS) -> "1"
    ap.get(Value.NEGATIVE_ATTITUDE_AND_CONCERNS) -> "2"
    else -> null
  }

  private fun q4(): Any? = when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION).value) {
    ap.get(Value.YES_SUPERVISION) -> "0"
    ap.get(Value.UNSURE_SUPERVISION) -> "1"
    ap.get(Value.NO_SUPERVISION) -> "2"
    else -> null
  }

  private fun q9(): Any? = when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION).value) {
    ap.get(Value.NO) -> "0"
    ap.get(Value.SOME) -> "1"
    ap.get(Value.YES) -> "2"
    else -> null
  }

  private fun q97(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).notes()

  private fun q98(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfSeriousHarm()

  private fun q99(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfReoffending()
}
