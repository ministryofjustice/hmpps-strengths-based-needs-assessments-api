package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class FinancialManagement : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o5-3" to ::q3,
      "o5-4" to ::q4,
      "o5-5" to ::q5,
      "o5-6" to ::q6,
      "o5-97" to ::q97,
      "o5-98" to ::q98,
      "o5-99" to ::q99,
      "o5_SAN_STRENGTH" to ::qStrength,
      "o5_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
  }

  private fun q3(): Any {
    return when(ap.answer(Field.FINANCE_MONEY_MANAGEMENT).value) {
      ap.get(Value.GOOD), ap.get(Value.FAIRLY_GOOD) -> "0"
      ap.get(Value.FAIRLY_BAD) -> "1"
      ap.get(Value.BAD) -> "2"
      else -> ""
    }
  }

  private fun q4(): Any {
    val income = ap.answer(Field.FINANCE_INCOME).values ?: return ""

    if (!income.contains(ap.get(Value.OFFENDING))) {
      return "0"
    }

    if (
      !income.contains(ap.get(Value.CARERS_ALLOWANCE))
      && !income.contains(ap.get(Value.DISABILITY_BENEFITS))
      && !income.contains(ap.get(Value.EMPLOYMENT))
      && !income.contains(ap.get(Value.FAMILY_OR_FRIENDS))
      && !income.contains(ap.get(Value.PENSION))
      && !income.contains(ap.get(Value.STUDENT_LOAN))
      && !income.contains(ap.get(Value.Undeclared))
      && !income.contains(ap.get(Value.WORK_RELATED_BENEFITS))
      && !income.contains(ap.get(Value.OTHER))
    ) {
      return "2"
    }

    return "1"
  }

  private fun q5(): Any {
    return ""
  }

  private fun q6(): Any {
    return ""
  }

  private fun q97(): Any {
    return ""
  }

  private fun q98(): Any {
    return ""
  }

  private fun q99(): Any {
    return ""
  }

  private fun qStrength(): Any {
    return ""
  }

  private fun qNotRelatedToRisk(): Any {
    return ""
  }
}
