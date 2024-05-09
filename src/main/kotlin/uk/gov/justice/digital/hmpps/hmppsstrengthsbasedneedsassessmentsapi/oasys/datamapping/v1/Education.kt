package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Education : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o4-2" to ::q2,
      "o4-3" to ::q3,
      "o4-4" to ::q4,
      "o4-5" to ::q5,
      "o4-7" to ::q7,
      "o4-7-1" to ::q71,
      "o4-8" to ::q8,
      "o4-9" to ::q9,
      "o4-10" to ::q10,
      "o4-94" to ::q94,
      "o4-96" to ::q96,
      "o4-98" to ::q98,
      "o4_SAN_STRENGTH" to ::qStrength,
      "o4_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
      "oSC2" to ::qSC2,
      "oSC2-t" to ::qSC2t,
      "oSC3" to ::qSC3,
      "oSC4" to ::qSC4,
      "oSC5" to ::qSC5,
      "oSC8" to ::qSC8,
    )
  }

  private fun q2(): Any {
    return when (ap.answer(Field.EMPLOYMENT_STATUS).value) {
      ap.get(Value.UNEMPLOYED_LOOKING_FOR_WORK), ap.get(Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK) -> "YES"
      ap.get(Value.EMPLOYED), ap.get(Value.SELF_EMPLOYED) -> "NO"
      ap.get(Value.RETIRED), ap.get(Value.CURRENTLY_UNAVAILABLE_FOR_WORK) -> "NA"
      else -> ""
    }
  }

  private fun getEmploymentHistory(): Any {
    return when (ap.answer(Field.EMPLOYMENT_HISTORY).value) {
      ap.get(Value.STABLE) -> "0"
      ap.get(Value.PERIODS_OF_INSTABILITY) -> "1"
      ap.get(Value.UNSTABLE) -> "2"
      else -> ""
    }
  }

  private fun q3(): Any {
    return when (ap.answer(Field.EMPLOYMENT_STATUS).value) {
      ap.get(Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK),
      ap.get(Value.UNEMPLOYED_LOOKING_FOR_WORK),
      -> {
        when (ap.answer(Field.HAS_BEEN_EMPLOYED).value) {
          ap.get(Value.NO) -> "2"
          else -> getEmploymentHistory()
        }
      }

      else -> getEmploymentHistory()
    }
  }

  private fun q4(): Any {
    return when (ap.answer(Field.EDUCATION_TRANSFERABLE_SKILLS).value) {
      ap.get(Value.NO) -> "2"
      ap.get(Value.YES_SOME_SKILLS) -> "1"
      ap.get(Value.YES) -> "0"
      else -> ""
    }
  }

  private fun q5(): Any {
    return when (ap.answer(Field.EMPLOYMENT_EXPERIENCE).value) {
      ap.get(Value.POSITIVE), ap.get(Value.MOSTLY_POSITIVE) -> "0"
      ap.get(Value.POSITIVE_AND_NEGATIVE) -> "1"
      ap.get(Value.NEGATIVE), ap.get(Value.MOSTLY_NEGATIVE) -> "2"
      else -> ""
    }
  }

  private fun getSeverityOf(field: Field): Int? {
    return when (ap.answer(field).value) {
      ap.get(Value.SIGNIFICANT_DIFFICULTIES) -> 2
      ap.get(Value.SOME_DIFFICULTIES) -> 1
      else -> null
    }
  }

  private fun q7(): Any {
    val difficulties = ap.answer(Field.EDUCATION_DIFFICULTIES).values.orEmpty()

    val categories = mapOf(
      ap.get(Value.READING) to Field.EDUCATION_DIFFICULTIES_READING_SEVERITY,
      ap.get(Value.WRITING) to Field.EDUCATION_DIFFICULTIES_WRITING_SEVERITY,
      ap.get(Value.NUMERACY) to Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY,
    )

    return when (true) {
      difficulties.contains(ap.get(Value.NONE)) -> "0"
      difficulties.isNotEmpty() ->
        categories
          .values.mapNotNull(::getSeverityOf)
          .maxOrNull()?.toString() ?: ""
      else -> ""
    }
  }

  private fun q71(): Any {
    val difficulties = ap.answer(Field.EDUCATION_DIFFICULTIES).values

    return difficulties?.joinToString(",") ?: ""
  }

  private fun q8(): Any {
    return when (ap.answer(Field.HEALTH_WELLBEING_LEARNING_DIFFICULTIES).value) {
      ap.get(Value.YES_SIGNIFICANT_DIFFICULTIES) -> "2"
      ap.get(Value.YES_SOME_DIFFICULTIES) -> "1"
      ap.get(Value.NO) -> "0"
      else -> ""
    }
  }

  private fun q9(): Any {
    return when (ap.answer(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED).value) {
      ap.get(Value.ENTRY_LEVEL) -> "2"
      ap.get(Value.LEVEL_1),
      ap.get(Value.LEVEL_2),
      ap.get(Value.LEVEL_3),
      ap.get(Value.LEVEL_4),
      ap.get(Value.LEVEL_5),
      ap.get(Value.LEVEL_6),
      ap.get(Value.LEVEL_7),
      ap.get(Value.LEVEL_8),
      -> "0"
      else -> ""
    }
  }

  private fun q10(): Any {
    return when (ap.answer(Field.EDUCATION_EXPERIENCE).value) {
      ap.get(Value.POSITIVE), ap.get(Value.MOSTLY_POSITIVE) -> "0"
      ap.get(Value.POSITIVE_AND_NEGATIVE) -> "1"
      ap.get(Value.NEGATIVE), ap.get(Value.MOSTLY_NEGATIVE) -> "2"
      else -> ""
    }
  }

  private fun q94(): Any {
    return listOf(
      // line 1
      when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
        ap.get(Value.YES) -> "Strengths and protective factor notes - "
        ap.get(Value.NO) -> "Area not linked to strengths and positive factors notes - "
        else -> ""
      } + (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS).value ?: ""),

      // line 2
      when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
        ap.get(Value.YES) -> "Area linked to serious harm notes - "
        ap.get(Value.NO) -> "Area not linked to serious harm notes - "
        else -> ""
      } + (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS).value ?: ""),

      // line 3
      when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
        ap.get(Value.YES) -> "Risk of reoffending notes - "
        ap.get(Value.NO) -> "Area not linked to reoffending notes - "
        else -> ""
      } + (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS).value ?: ""),
    ).filterNot { it.isEmpty() }.joinToString(separator = "\n")
  }

  private fun q96(): Any {
    return when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun q98(): Any {
    return when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qStrength(): Any {
    return when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qNotRelatedToRisk(): Any {
    return when (ap.answer(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qSC2(): Any {
    return when (ap.answer(Field.EDUCATION_PROFESSIONAL_OR_VOCATIONAL_QUALIFICATIONS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> ""
    }
  }

  private fun qSC2t(): Any {
    return ap.answer(Field.EDUCATION_PROFESSIONAL_OR_VOCATIONAL_QUALIFICATIONS_YES_DETAILS).value.orEmpty()
  }

  private fun qSC3(): Any {
    return when (ap.answer(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED).value) {
      ap.get(Value.ENTRY_LEVEL) -> "ANYOTHER"
      ap.get(Value.LEVEL_1),
      ap.get(Value.LEVEL_2),
      ap.get(Value.LEVEL_3),
      ap.get(Value.LEVEL_4),
      ap.get(Value.LEVEL_5),
      ap.get(Value.LEVEL_6),
      ap.get(Value.LEVEL_7),
      ap.get(Value.LEVEL_8),
      -> "MATHSENGLISH"
      ap.get(Value.NONE_OF_THESE) -> "NOQUAL"
      else -> ""
    }
  }

  private fun qSC4(): Any {
    return when (ap.answer(Field.EMPLOYMENT_STATUS).value) {
      ap.get(Value.RETIRED) -> "FULLTIME"
      ap.get(Value.EMPLOYED) -> when (ap.answer(Field.EMPLOYMENT_TYPE).value) {
        ap.get(Value.FULL_TIME) -> "FULLTIME"
        ap.get(Value.PART_TIME),
        ap.get(Value.TEMPORARY_OR_CASUAL),
        ap.get(Value.APPRENTICESHIP),
        -> "PARTTIME"
        else -> ""
      }
      ap.get(Value.CURRENTLY_UNAVAILABLE_FOR_WORK) -> when (ap.answer(Field.HAS_BEEN_EMPLOYED).value) {
        ap.get(Value.NO) -> "UNEMPLOYED"
        else -> ""
      }
      ap.get(Value.UNEMPLOYED_LOOKING_FOR_WORK),
      ap.get(Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK),
      -> when (ap.answer(Field.HAS_BEEN_EMPLOYED).value) {
        ap.get(Value.NO) -> "0"
        else -> ""
      }
      else -> ""
    }
  }

  private fun qSC5(): Any {
    return when (ap.answer(Field.EMPLOYMENT_STATUS).value) {
      ap.get(Value.EMPLOYED),
      ap.get(Value.SELF_EMPLOYED),
      -> "Yes [Score 0]"
      ap.get(Value.UNEMPLOYED_LOOKING_FOR_WORK),
      ap.get(Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK),
      -> "No [Score 0]"
      else -> ""
    }
  }

  private fun qSC8(): Any {
    return when (ap.answer(Field.FINANCE_MONEY_MANAGEMENT).value) {
      ap.get(Value.GOOD) -> "YES"
      ap.get(Value.FAIRLY_GOOD) -> "SOMETIMES"
      ap.get(Value.FAIRLY_BAD),
      ap.get(Value.BAD),
      -> "NOTCONFIDENT"
      else -> ""
    }
  }
}
