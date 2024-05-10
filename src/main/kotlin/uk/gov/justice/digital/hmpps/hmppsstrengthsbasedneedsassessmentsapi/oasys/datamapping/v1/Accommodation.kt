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
      "o3-5" to ::q5,
      "o3-6" to ::q6,
      "o3-97" to ::q97,
      "o3-98" to ::q98,
      "o3-99" to ::q99,
      "o3_SAN_STRENGTH" to ::qStrength,
      "o3_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
  }

  private fun q3(): Any? {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "YES"
      ap.get(Value.TEMPORARY), ap.get(Value.SETTLED) -> "NO"
      else -> null
    }
  }

  private fun q4(): Any? {
    val noAccommodation = ap.answer(Field.CURRENT_ACCOMMODATION).value == ap.get(Value.NO_ACCOMMODATION)
    if (noAccommodation) {
      return "2"
    }
    return when (ap.answer(Field.SUITABLE_HOUSING).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.YES_WITH_CONCERNS) -> "1"
      ap.get(Value.NO) -> "2"
      else -> null
    }
  }

  private fun q5(): Any? {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "2"
      ap.get(Value.TEMPORARY) -> when (ap.answer(Field.TYPE_OF_TEMPORARY_ACCOMMODATION).value) {
        ap.get(Value.SHORT_TERM) -> "2"
        else -> null
      }
      else -> null
    }
  }

  private fun q6(): Any? {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "2"
      else -> when (ap.answer(Field.SUITABLE_HOUSING_LOCATION).value) {
        ap.get(Value.YES) -> "0"
        ap.get(Value.NO) -> "2"
        else -> null
      }
    }
  }

  private fun q97(): Any? {
    return PractitionerAnalysis("ACCOMMODATION", ap).notes()
  }

  private fun q98(): Any? {
    return PractitionerAnalysis("ACCOMMODATION", ap).riskOfSeriousHarm()
  }

  private fun q99(): Any? {
    return PractitionerAnalysis("ACCOMMODATION", ap).riskOfReoffending()
  }

  private fun qStrength(): Any? {
    return PractitionerAnalysis("ACCOMMODATION", ap).strengthsOrProtectiveFactors()
  }

  private fun qNotRelatedToRisk(): Any? {
    return PractitionerAnalysis("ACCOMMODATION", ap).relatedToRisk()
  }
}
