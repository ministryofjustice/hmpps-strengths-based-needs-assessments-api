package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.AnswersProvider

class PractitionerAnalysis(private val sectionPrefix: String, private val ap: AnswersProvider) {
  private fun strengthsOrProtectiveFactorsNotes(): String? {
    val yesDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_YES_DETAILS")).value
    val noDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_NO_DETAILS")).value

    return when {
      !yesDetails.isNullOrBlank() -> "Strengths and protective factor notes - $yesDetails"
      !noDetails.isNullOrBlank() -> "Area not linked to strengths and positive factors notes - $noDetails"
      else -> null
    }
  }

  private fun seriousHarmNotes(): String? {
    val yesDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_YES_DETAILS")).value
    val noDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_NO_DETAILS")).value

    return when {
      !yesDetails.isNullOrBlank() -> "Area linked to serious harm notes - $yesDetails"
      !noDetails.isNullOrBlank() -> "Area not linked to serious harm notes - $noDetails"
      else -> null
    }
  }

  private fun reoffendingNotes(): String? {
    val yesDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_YES_DETAILS")).value
    val noDetails =
      ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_NO_DETAILS")).value

    return when {
      !yesDetails.isNullOrBlank() -> "Risk of reoffending notes - $yesDetails"
      !noDetails.isNullOrBlank() -> "Area not linked to reoffending notes - $noDetails"
      else -> null
    }
  }

  fun notes(): Any? = listOfNotNull(
    strengthsOrProtectiveFactorsNotes(),
    seriousHarmNotes(),
    reoffendingNotes(),
  ).joinToString(separator = "\n").ifEmpty { null }

  fun riskOfSeriousHarm(): Any? = when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM")).value) {
    ap.get(Value.YES) -> "YES"
    ap.get(Value.NO) -> "NO"
    else -> null
  }

  fun riskOfReoffending(): Any? = when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING")).value) {
    ap.get(Value.YES) -> "YES"
    ap.get(Value.NO) -> "NO"
    else -> null
  }

  fun strengthsOrProtectiveFactors(): Any? = when (ap.answer(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS")).value) {
    ap.get(Value.YES) -> "YES"
    ap.get(Value.NO) -> "NO"
    else -> null
  }
}
