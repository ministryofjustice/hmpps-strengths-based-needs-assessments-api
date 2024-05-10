package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.AnswersProvider

class PractitionerAnalysis(private val sectionPrefix: String, private val ap: AnswersProvider) {
  private fun strenghtsOrProtectiveFactorsNotes(): String? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS")).value) {
      ap.get(Value.YES) -> "Strengths and protective factor notes - "
      ap.get(Value.NO) -> "Area not linked to strengths and positive factors notes - "
      else -> null
    }?.let { prefix ->
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS")).value?.let {
        prefix + it
      }
    }
  }

  private fun seriousHarmNotes(): String? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM")).value) {
      ap.get(Value.YES) -> "Area linked to serious harm notes - "
      ap.get(Value.NO) -> "Area not linked to serious harm notes - "
      else -> null
    }?.let { prefix ->
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS")).value?.let {
        prefix + it
      }
    }
  }

  private fun reoffendingNotes(): String? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING")).value) {
      ap.get(Value.YES) -> "Risk of reoffending notes - "
      ap.get(Value.NO) -> "Area not linked to reoffending notes - "
      else -> null
    }?.let { prefix ->
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS")).value?.let {
        prefix + it
      }
    }
  }

  fun notes(): Any? {
    return listOfNotNull(
      strenghtsOrProtectiveFactorsNotes(),
      seriousHarmNotes(),
      reoffendingNotes(),
    ).joinToString(separator = "\n").ifEmpty { null }
  }

  fun riskOfSeriousHarm(): Any? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM")).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  fun riskOfReoffending(): Any? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING")).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  fun strengthsOrProtectiveFactors(): Any? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS")).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  fun relatedToRisk(): Any? {
    return when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RELATED_TO_RISK")).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }
}
