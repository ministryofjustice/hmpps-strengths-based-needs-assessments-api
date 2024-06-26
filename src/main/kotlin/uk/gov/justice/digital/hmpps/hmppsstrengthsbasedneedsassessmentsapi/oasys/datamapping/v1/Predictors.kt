package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Predictors : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o1-30" to ::q30,
    )
  }

  private fun q30(): Any? {
    return when {
      ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.contains(ap.get(Value.SEXUAL_ELEMENT)) -> "YES"
      ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.contains(ap.get(Value.SEXUAL_DESIRES)) -> "YES"
      else -> null
    }
  }
}
