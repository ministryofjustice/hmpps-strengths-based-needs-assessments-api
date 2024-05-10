package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class EmotionalWellbeing : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o10-1" to ::q1,
      "o10-2" to ::q2,
      "o10-4" to ::q4,
      "o10-5" to ::q5,
      "o10-6" to ::q6,
      "o10-7_V2_CHILDHOOD" to ::qChildhoodBehaviouralProblems,
      "o10-7-V2-HISTHEADINJ" to ::qHistoryOfHeadInjury,
      "o10.7_V2_HISTPSYCH" to ::qHistoryOfPsychTreatment,
      "o10.7_V2_PSYCHTREAT" to ::qCurrentPsychTreatment,
      "o10-97" to ::q97,
      "o10-98" to ::q98,
      "o10-99" to ::q99,
      "o10_SAN_STRENGTH" to ::qStrength,
      "o10_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
  }

  private fun q1(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_COPING_DAY_TO_DAY_LIFE).value) {
      ap.get(Value.YES) -> "0"
      ap.get(Value.YES_SOME_DIFFICULTIES) -> "1"
      ap.get(Value.NO) -> "2"
      else -> ""
    }
  }

  private fun q2(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value) {
      ap.get(Value.YES_ONGOING_SEVERE) -> "2"
      ap.get(Value.YES_ONGOING), ap.get(Value.YES_IN_THE_PAST) -> "1"
      ap.get(Value.NO) -> "0"
      else -> ""
    }
  }

  private fun q4(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_ATTITUDE_TOWARDS_SELF).value) {
      ap.get(Value.POSITIVE) -> "0"
      ap.get(Value.SOME_NEGATIVE_ASPECTS) -> "1"
      ap.get(Value.NEGATIVE) -> "2"
      else -> ""
    }
  }

  private fun q5(): Any {
    val values = listOf(
      when (ap.answer(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS).value) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> ""
      },
      when (ap.answer(Field.HEALTH_WELLBEING_SELF_HARMED).value) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> ""
      },
    )

    return when (true) {
      values.any { it == "YES" } -> "YES"
      values.any { it == "NO" } -> "NO"
      else -> ""
    }
  }

  private fun q6(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value) {
      ap.get(Value.YES_ONGOING_SEVERE) -> "2"
      ap.get(Value.YES_ONGOING), ap.get(Value.YES_IN_THE_PAST) -> "1"
      ap.get(Value.NO) -> "0"
      else -> ""
    }
  }

  private fun qChildhoodBehaviouralProblems(): Any {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD_BEHAVIOUR).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qHistoryOfHeadInjury(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_HEAD_INJURY_OR_ILLNESS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qHistoryOfPsychTreatment(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION).value) {
      ap.get(Value.YES_ONGOING_SEVERE), ap.get(Value.YES_ONGOING), ap.get(Value.YES_IN_THE_PAST) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qCurrentPsychTreatment(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT).value) {
      ap.get(Value.YES), ap.get(Value.PENDING_TREATMENT) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun q97(): Any {
    return listOf(
      // line 1
      when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
        ap.get(Value.YES) -> "Strengths and protective factor notes - "
        ap.get(Value.NO) -> "Area not linked to strengths and positive factors notes - "
        else -> ""
      } + (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS).value ?: ""),

      // line 2
      when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
        ap.get(Value.YES) -> "Area linked to serious harm notes - "
        ap.get(Value.NO) -> "Area not linked to serious harm notes - "
        else -> ""
      } + (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS).value ?: ""),

      // line 3
      when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
        ap.get(Value.YES) -> "Risk of reoffending notes - "
        ap.get(Value.NO) -> "Area not linked to reoffending notes - "
        else -> ""
      } + (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS).value ?: ""),
    ).filterNot { it.isEmpty() }.joinToString(separator = "\n")
  }

  private fun q98(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun q99(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qStrength(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qNotRelatedToRisk(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_PRACTITIONER_ANALYSIS_RELATED_TO_RISK).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }
}
