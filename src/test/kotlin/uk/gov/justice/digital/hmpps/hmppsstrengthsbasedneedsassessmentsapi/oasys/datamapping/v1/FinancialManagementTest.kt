package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class FinancialManagementTest : SectionMappingTest(FinancialManagement(), "1.0") {
  @Test
  fun q3() {
    test(
      "o5-3",
      Given().expect(null),
      Given(Field.FINANCE_MONEY_MANAGEMENT, null).expect(null),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.GOOD).expect("0"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_GOOD).expect("0"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_BAD).expect("1"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.BAD).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o5-4",
      Given().expect(null),
      Given(Field.FINANCE_INCOME, emptyList()).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.CARERS_ALLOWANCE)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.DISABILITY_BENEFITS)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.EMPLOYMENT)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.PENSION)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.STUDENT_LOAN)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.Undeclared)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.WORK_RELATED_BENEFITS)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.OTHER)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.NO_MONEY)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.CARERS_ALLOWANCE)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.DISABILITY_BENEFITS)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.EMPLOYMENT)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.FAMILY_OR_FRIENDS)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.PENSION)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.STUDENT_LOAN)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.Undeclared)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.WORK_RELATED_BENEFITS)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING, Value.OTHER)).expect("1"),
      Given(Field.FINANCE_INCOME, listOf(Value.OFFENDING)).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o5-5",
      Given().expect(null),
      Given(Field.FINANCE_INCOME, emptyList()).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.STUDENT_LOAN)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS))
        .and(Field.FAMILY_OR_FRIENDS_DETAILS, Value.NO).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS))
        .and(Field.FAMILY_OR_FRIENDS_DETAILS, Value.YES).expect("2"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS))
        .and(Field.FAMILY_OR_FRIENDS_DETAILS, Value.UNKNOWN).expect("M"),
    )
  }

  @Test
  fun q6() {
    test(
      "o5-6",
      Given().expect(null),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.BAD).expect(null),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_BAD).expect(null),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.GOOD).expect("0"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_GOOD).expect("0"),
    )
  }

  @Test
  fun q97() {
    test(
      "o5-97",
      *PractitionerAnalysisScenarios("FINANCE").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o5-98",
      *PractitionerAnalysisScenarios("FINANCE").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o5-99",
      *PractitionerAnalysisScenarios("FINANCE").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o5_SAN_STRENGTH",
      *PractitionerAnalysisScenarios("FINANCE").strengthsOrProtectiveFactors(),
    )
  }
}
