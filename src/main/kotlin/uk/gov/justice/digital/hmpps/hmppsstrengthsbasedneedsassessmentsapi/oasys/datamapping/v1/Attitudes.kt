package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Attitudes : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o12-1" to ::q1,
      "o12-3" to ::q3,
      "o12-4" to ::q4,
      "o12-9" to ::q9,
      "o12-97" to ::q97,
      "o12-98" to ::q98,
      "o12-99" to ::q99,
    )
  }

  private fun q1(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR).value) {
      ap.get(Value.NO) -> "0"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.YES) -> "2"
      else -> ""
    }
  }

  private fun q3(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE).value) {
      ap.get(Value.YES_POSITIVE) -> "0"
      ap.get(Value.NEGATIVE_ATTITUDE_NO_CONCERNS) -> "1"
      ap.get(Value.NEGATIVE_ATTITUDE_AND_CONCERNS) -> "2"
      else -> ""
    }
  }

  private fun q4(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION).value) {
      ap.get(Value.YES_SUPERVISION) -> "0"
      ap.get(Value.UNSURE_SUPERVISION) -> "1"
      ap.get(Value.NO_SUPERVISION) -> "2"
      else -> ""
    }
  }

  private fun q9(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION).value) {
      ap.get(Value.NO) -> "0"
      ap.get(Value.SOME) -> "1"
      ap.get(Value.YES) -> "2"
      else -> ""
    }
  }

  private fun q97(): Any {
    return listOf(
      // line 1
      when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
        ap.get(Value.YES) -> "Strengths and protective factor notes - "
        ap.get(Value.NO) -> "Area not linked to strengths and positive factors notes - "
        else -> ""
      } + (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS).value ?: ""),

      // line 2
      when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
        ap.get(Value.YES) -> "Area linked to serious harm notes - "
        ap.get(Value.NO) -> "Area not linked to serious harm notes - "
        else -> ""
      } + (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS).value ?: ""),

      // line 3
      when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
        ap.get(Value.YES) -> "Risk of reoffending notes - "
        ap.get(Value.NO) -> "Area not linked to reoffending notes - "
        else -> ""
      } + (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS).value ?: ""),
    ).filterNot { it.isEmpty() }.joinToString(separator = "\n")
  }

  private fun q98(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun q99(): Any {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }
}
