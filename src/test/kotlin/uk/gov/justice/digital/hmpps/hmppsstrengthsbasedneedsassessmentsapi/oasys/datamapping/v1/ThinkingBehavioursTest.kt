package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class ThinkingBehavioursTest : SectionMappingTest(ThinkingBehaviours(), "1.0") {
  @Test
  fun q2() {
    test(
      "o11-2",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.YES).expect("2"),
    )
  }

  @Test
  fun q3() {
    test(
      "o11-3",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.NO_VIOLENCE).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.YES_VIOLENCE).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o11-4",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.NO).expect("2"),
    )
  }

  @Test
  fun q6() {
    test(
      "o11-6",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.LIMITED_PROBLEM_SOLVING).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.NO).expect("2"),
    )
  }

  @Test
  fun q7() {
    test(
      "o11-7",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.NO).expect("2"),
    )
  }

  @Test
  fun q9() {
    test(
      "o11-9",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.NO).expect("2"),
    )
  }

  @Test
  fun q11() {
    test(
      "o11-11",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.YES).expect("2"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.NO).expect("0"),
    )
  }

  @Test
  fun q12() {
    test(
      "o11-12",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST, null).expect(""),
      Given(
        Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST,
        Value.YES_OFFENCE_RELATED_SEXUAL_INTEREST,
      ).expect("2"),
      Given(
        Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST,
        Value.SOME_OFFENCE_RELATED_SEXUAL_INTEREST,
      ).expect("1"),
      Given(
        Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST,
        Value.NO_OFFENCE_RELATED_SEXUAL_INTEREST,
      ).expect("0"),
    )
  }

  @Test
  fun q97() {
    test(
      "o11-97",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(
          Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS,
          "Details 2 go here",
        )
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(
          Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS,
          "Details 1 go here",
        )
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area not linked to serious harm notes - 
          Area not linked to reoffending notes - 
          """.trimIndent(),
        ),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(
          Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS,
          "Details 2 go here",
        )
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(
          Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS,
          "Details 1 go here",
        )
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(
          Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS,
          "Details 2 go here",
        )
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
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
      "o11-98",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q99() {
    test(
      "o11-99",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }
}
