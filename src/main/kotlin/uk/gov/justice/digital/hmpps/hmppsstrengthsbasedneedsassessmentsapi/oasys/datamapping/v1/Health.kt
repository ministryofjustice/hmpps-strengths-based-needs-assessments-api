package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Health : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o13-1" to ::q1,
    )
  }

  private fun q1(): Any? {
    return when {
      ap.answer(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION).value == ap.get(Value.YES) ||
        ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value == ap.get(Value.YES_ONGOING) ||
        ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value == ap.get(Value.YES_ONGOING_SEVERE) ||
        ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value == ap.get(Value.YES_IN_THE_PAST)
      -> "YES"
      ap.answer(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION).value == ap.get(Value.NO) ||
        ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value == ap.get(Value.NO)
      -> "NO"
      else -> null
    }
  }
}
