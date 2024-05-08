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
      Given().expect(""),
      Given(Field.FINANCE_MONEY_MANAGEMENT, null).expect(""),
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
      Given().expect(""),
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
      Given().expect(""),
      Given(Field.FINANCE_INCOME, emptyList()).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.STUDENT_LOAN)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS)).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS))
        .and(Field.FAMILY_OR_FRIENDS_DETAILS, Value.NO).expect("0"),
      Given(Field.FINANCE_INCOME, listOf(Value.FAMILY_OR_FRIENDS))
        .and(Field.FAMILY_OR_FRIENDS_DETAILS, Value.YES).expect("2"),
    )
  }

  @Test
  fun q6() {
    test(
      "o5-6",
      Given().expect(""),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.BAD).expect(""),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_BAD).expect(""),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.GOOD).expect("0"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_GOOD).expect("0"),
    )
  }

  @Test
  fun q97() {
    test(
      "o5-97",
      Given().expect(""),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area not linked to serious harm notes - 
          Area not linked to reoffending notes - 
          """.trimIndent(),
        ),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
    )
  }

  @Test
  fun q98() {
    test(
      "o5-98",
      Given().expect(""),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q99() {
    test(
      "o5-99",
      Given().expect(""),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o5_SAN_STRENGTH",
      Given().expect(""),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES).expect("YES"),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qNotRelatedToRisk() {
    test(
      "o5_SAN_NOT_REL_RISK",
      Given().expect(""),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.YES).expect("YES"),
      Given(Field.FINANCE_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.NO).expect("NO"),
    )
  }
}
