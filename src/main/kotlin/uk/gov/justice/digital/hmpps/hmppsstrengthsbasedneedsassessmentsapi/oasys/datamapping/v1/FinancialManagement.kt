package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class FinancialManagement : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap = mapOf(
    "o5-3" to ::q3,
    "o5-4" to ::q4,
    "o5-5" to ::q5,
    "o5-6" to ::q6,
    "o5-97" to ::q97,
    "o5-98" to ::q98,
    "o5-99" to ::q99,
    "o5_SAN_STRENGTH" to ::qStrength,
  )

  private fun q3(): Any? = when (ap.answer(Field.FINANCE_MONEY_MANAGEMENT).value) {
    ap.get(Value.GOOD), ap.get(Value.FAIRLY_GOOD) -> "0"
    ap.get(Value.FAIRLY_BAD) -> "1"
    ap.get(Value.BAD) -> "2"
    else -> null
  }

  private fun q4(): Any? {
    val income = ap.answer(Field.FINANCE_INCOME).values
    val nonOffendingIncomes = setOf(
      Value.CARERS_ALLOWANCE,
      Value.DISABILITY_BENEFITS,
      Value.EMPLOYMENT,
      Value.FAMILY_OR_FRIENDS,
      Value.PENSION,
      Value.STUDENT_LOAN,
      Value.Undeclared,
      Value.WORK_RELATED_BENEFITS,
      Value.OTHER,
    ).map { ap.get(it) }

    return when {
      (income == null) -> null
      (!income.contains(ap.get(Value.OFFENDING))) -> "0"
      (income.any { it in nonOffendingIncomes }) -> "1"
      else -> "2"
    }
  }

  private fun q5(): Any? {
    val income = ap.answer(Field.FINANCE_INCOME).values
    return when (income?.contains(ap.get(Value.FAMILY_OR_FRIENDS))) {
      null -> null
      true -> when (ap.answer(Field.FAMILY_OR_FRIENDS_DETAILS).value) {
        ap.get(Value.YES) -> "2"
        else -> "0"
      }
      else -> "0"
    }
  }

  private fun q6(): Any? = when (ap.answer(Field.FINANCE_MONEY_MANAGEMENT).value) {
    ap.get(Value.GOOD), ap.get(Value.FAIRLY_GOOD) -> "0"
    else -> null
  }

  private fun q97(): Any? = PractitionerAnalysis("FINANCE", ap).notes()

  private fun q98(): Any? = PractitionerAnalysis("FINANCE", ap).riskOfSeriousHarm()

  private fun q99(): Any? = PractitionerAnalysis("FINANCE", ap).riskOfReoffending()

  private fun qStrength(): Any? = PractitionerAnalysis("FINANCE", ap).strengthsOrProtectiveFactors()
}
