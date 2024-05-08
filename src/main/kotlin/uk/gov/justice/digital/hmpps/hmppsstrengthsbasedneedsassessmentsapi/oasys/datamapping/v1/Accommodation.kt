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

  private fun q3(): String {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "YES"
      ap.get(Value.TEMPORARY), ap.get(Value.SETTLED) -> "NO"
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

  private fun q5(): Any {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "2"
      ap.get(Value.TEMPORARY) -> when (ap.answer(Field.TYPE_OF_TEMPORARY_ACCOMMODATION).value) {
        ap.get(Value.SHORT_TERM) -> "2"
        else -> ""
      }
      else -> ""
    }
  }

  private fun q6(): Any {
    return when (ap.answer(Field.CURRENT_ACCOMMODATION).value) {
      ap.get(Value.NO_ACCOMMODATION) -> "2"
      else -> when (ap.answer(Field.SUITABLE_HOUSING_LOCATION).value) {
        ap.get(Value.YES) -> "0"
        ap.get(Value.NO) -> "2"
        else -> ""
      }
    }
  }

  private fun q97(): Any {
    return listOf(
      // line 1
      when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
        ap.get(Value.YES) -> "Strengths and protective factor notes - "
        ap.get(Value.NO) -> "Area not linked to strengths and positive factors notes - "
        else -> ""
      } + (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS).value ?: ""),

      // line 2
      when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
        ap.get(Value.YES) -> "Area linked to serious harm notes - "
        ap.get(Value.NO) -> "Area not linked to serious harm notes - "
        else -> ""
      } + (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS).value ?: ""),

      // line 3
      when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
        ap.get(Value.YES) -> "Risk of reoffending notes - "
        ap.get(Value.NO) -> "Area not linked to reoffending notes - "
        else -> ""
      } + (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS).value ?: ""),
    ).filterNot { it.isEmpty() }.joinToString(separator = "\n")
  }

  private fun q98(): Any {
    return when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun q99(): Any {
    return when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qStrength(): Any {
    return when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qNotRelatedToRisk(): Any {
    return when (ap.answer(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }
}
