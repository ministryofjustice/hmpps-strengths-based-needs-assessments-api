package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class OffenceAnalysis : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o2-1" to ::q1,
      "o2-2_V2_ANYVIOL" to ::q2Violence,
      "o2-2_V2_DOM_ABUSE" to ::q2DomesticAbuse,
      "o2-2_V2_EXCESSIVE" to ::q2ExcessiveViolence,
      "o2-2_V2_SEXUAL" to ::q2Sexual,
      "o2-6" to ::q6,
      "o2-7-2" to ::q72,
      "o2-8" to ::q8,
      "o2-9_V2_SEXUAL" to ::q9Sexual,
      "o2-9_V2_FINANCIAL" to ::q9Financial,
      "o2-9_V2_ADDICTION" to ::q9Addiction,
      "o2-9_V2_EMOTIONAL" to ::q9Emotional,
      "o2-12" to ::q12,
      "o2-99" to ::q99,
    )
  }

  private fun q1(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE).value
  }

  private fun q2Violence(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.VIOLENCE_OR_COERCION))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q2DomesticAbuse(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.DOMESTIC_ABUSE))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q2ExcessiveViolence(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.EXCESSIVE_OR_SADISTIC_VIOLENCE))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q2Sexual(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.SEXUAL_ELEMENT))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q6(): Any? {
    return when (ap.answer(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  private fun q72(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.let {
      if (it.contains(ap.get(Value.PRESSURISED))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q8(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_REASON).value
  }

  private fun q9Sexual(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.let {
      if (it.contains(ap.get(Value.SEXUAL_DESIRES))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q9Financial(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.let {
      if (it.contains(ap.get(Value.BASIC_FINANCIAL_NEEDS))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q9Addiction(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.let {
      if (it.contains(ap.get(Value.SUPPORTING_DRUG_USE))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q9Emotional(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_GAIN).values?.let {
      if (it.contains(ap.get(Value.EMOTIONS_CLOUDED_JUDGEMENT))) {
        "YES"
      } else {
        "NO"
      }
    }
  }

  private fun q12(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING).value
  }

  private fun q99(): Any? {
    return when (ap.answer(Field.OFFENCE_ANALYSIS_RISK).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }
}
