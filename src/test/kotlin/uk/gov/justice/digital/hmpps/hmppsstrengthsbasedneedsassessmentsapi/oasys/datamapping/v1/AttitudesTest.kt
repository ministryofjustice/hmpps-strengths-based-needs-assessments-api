package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class AttitudesTest : SectionMappingTest(Attitudes(), "1.0") {
  @Test
  fun q1() {
    test(
      "o12-1",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.YES).expect("2"),
    )
  }

  @Test
  fun q3() {
    test(
      "o12-3",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.YES_POSITIVE).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.NEGATIVE_ATTITUDE_NO_CONCERNS).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.NEGATIVE_ATTITUDE_AND_CONCERNS).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o12-4",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.YES_SUPERVISION).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.UNSURE_SUPERVISION).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.NO_SUPERVISION).expect("2"),
    )
  }

  @Test
  fun q9() {
    test(
      "o12-9",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.SOME).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.YES).expect("2"),
    )
  }

  @Test
  fun q97() {
    test(
      "o12-97",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
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
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
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
        .and(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
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
      "o12-98",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q99() {
    test(
      "o12-99",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }
}
