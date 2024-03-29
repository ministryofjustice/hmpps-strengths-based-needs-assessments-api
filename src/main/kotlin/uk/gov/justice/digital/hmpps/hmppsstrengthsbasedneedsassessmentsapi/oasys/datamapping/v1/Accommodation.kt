package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Accommodation : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o3-3" to ::q3,
      "o3-4" to ::q4,
      "o3-98" to ::q98,
      "o3-99" to ::q99,
    )
  }

  private fun q3(): Any {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.TEMPORARY), ap.get(Value.NO_ACCOMMODATION) -> "YES"
      ap.get(Value.SETTLED) -> "NO"
      else -> ""
    }
  }

  private fun q4(): Any {
    val noAccommodation = ap.answer(Field.CURRENT_ACCOMMODATION).value == ap.get(Value.NO_ACCOMMODATION)
    if (noAccommodation) {
      return "2"
    }
    return when (ap.answer(Field.SUITABLE_HOUSING).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.YES_WITH_CONCERNS) -> "1"
      ap.get(Value.NO) -> "2"
      else -> ""
    }
  }

  private fun q98(): Any {
    return ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value ?: ""
  }

  private fun q99(): Any {
    return ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value ?: ""
  }
}
