package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class EducationTest : SectionMappingTest(Education(), "1.0") {
  @Test
  fun q2() {
    test(
      "o4-2",
      Given().expect(""),
      Given(Field.EMPLOYMENT_STATUS, null).expect(""),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_LOOKING_FOR_WORK).expect("YES"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK).expect("YES"),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED).expect("NO"),
      Given(Field.EMPLOYMENT_STATUS, Value.SELF_EMPLOYED).expect("NO"),
      Given(Field.EMPLOYMENT_STATUS, Value.RETIRED).expect("NA"),
      Given(Field.EMPLOYMENT_STATUS, Value.CURRENTLY_UNAVAILABLE_FOR_WORK).expect("NA"),
    )
  }

  @Test
  fun q3() {
    test(
      "o4-3",
      Given().expect(""),
      Given(Field.EMPLOYMENT_STATUS, null).expect(""),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK)
        .and(Field.HAS_BEEN_EMPLOYED, Value.NO)
        .expect("2"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_LOOKING_FOR_WORK)
        .and(Field.HAS_BEEN_EMPLOYED, Value.NO)
        .expect("2"),
      Given(Field.EMPLOYMENT_HISTORY, Value.STABLE).expect("0"),
      Given(Field.EMPLOYMENT_HISTORY, Value.PERIODS_OF_INSTABILITY).expect("1"),
      Given(Field.EMPLOYMENT_HISTORY, Value.UNSTABLE).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o4-4",
      Given().expect(""),
      Given(Field.EDUCATION_TRANSFERABLE_SKILLS, null).expect(""),
      Given(Field.EDUCATION_TRANSFERABLE_SKILLS, Value.NO).expect("2"),
      Given(Field.EDUCATION_TRANSFERABLE_SKILLS, Value.YES_SOME_SKILLS).expect("1"),
      Given(Field.EDUCATION_TRANSFERABLE_SKILLS, Value.YES).expect("0"),
    )
  }

  @Test
  fun q5() {
    test(
      "o4-5",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EXPERIENCE, null).expect(""),
      Given(Field.EMPLOYMENT_EXPERIENCE, Value.POSITIVE).expect("0"),
      Given(Field.EMPLOYMENT_EXPERIENCE, Value.MOSTLY_POSITIVE).expect("0"),
      Given(Field.EMPLOYMENT_EXPERIENCE, Value.POSITIVE_AND_NEGATIVE).expect("1"),
      Given(Field.EMPLOYMENT_EXPERIENCE, Value.MOSTLY_NEGATIVE).expect("2"),
      Given(Field.EMPLOYMENT_EXPERIENCE, Value.NEGATIVE).expect("2"),
    )
  }

  @Test
  fun q7() {
    test(
      "o4-7",
      Given().expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, emptyList()).expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NONE)).expect("0"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING))
        .and(Field.EDUCATION_DIFFICULTIES_READING_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING))
        .and(Field.EDUCATION_DIFFICULTIES_READING_SEVERITY, Value.SIGNIFICANT_DIFFICULTIES)
        .expect("2"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING))
        .and(Field.EDUCATION_DIFFICULTIES_WRITING_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.WRITING))
        .and(Field.EDUCATION_DIFFICULTIES_WRITING_SEVERITY, Value.SIGNIFICANT_DIFFICULTIES)
        .expect("2"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NUMERACY))
        .and(Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NUMERACY))
        .and(Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY, Value.SIGNIFICANT_DIFFICULTIES)
        .expect("2"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING))
        .and(Field.EDUCATION_DIFFICULTIES_READING_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING))
        .and(Field.EDUCATION_DIFFICULTIES_READING_SEVERITY, Value.SOME_DIFFICULTIES)
        .expect("1"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.WRITING))
        .and(Field.EDUCATION_DIFFICULTIES_WRITING_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.WRITING))
        .and(Field.EDUCATION_DIFFICULTIES_WRITING_SEVERITY, Value.SOME_DIFFICULTIES)
        .expect("1"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NUMERACY))
        .and(Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY, null)
        .expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NUMERACY))
        .and(Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY, Value.SOME_DIFFICULTIES)
        .expect("1"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING, Value.NUMERACY))
        .and(Field.EDUCATION_DIFFICULTIES_READING_SEVERITY, Value.SIGNIFICANT_DIFFICULTIES)
        .and(Field.EDUCATION_DIFFICULTIES_NUMERACY_SEVERITY, Value.SOME_DIFFICULTIES)
        .expect("2"),
    )
  }

  @Test
  fun q71() {
    test(
      "o4-7-1",
      Given().expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, emptyList()).expect(""),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING)).expect("READING"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.WRITING)).expect("WRITING"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.NUMERACY)).expect("NUMERACY"),
      Given(Field.EDUCATION_DIFFICULTIES, listOf(Value.READING, Value.WRITING, Value.NUMERACY))
        .expect("READING,WRITING,NUMERACY"),
    )
  }

  @Test
  fun q8() {
    test(
      "o4-8",
      Given().expect(""),
      Given(Field.HEALTH_WELLBEING_LEARNING_DIFFICULTIES, null).expect(""),
      Given(Field.HEALTH_WELLBEING_LEARNING_DIFFICULTIES, Value.YES_SIGNIFICANT_DIFFICULTIES).expect("2"),
      Given(Field.HEALTH_WELLBEING_LEARNING_DIFFICULTIES, Value.YES_SOME_DIFFICULTIES).expect("1"),
      Given(Field.HEALTH_WELLBEING_LEARNING_DIFFICULTIES, Value.NO).expect("0"),
    )
  }

  @Test
  fun q9() {
    test(
      "o4-9",
      Given().expect(""),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, null).expect(""),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.ENTRY_LEVEL).expect("2"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_1).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_2).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_3).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_4).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_5).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_6).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_7).expect("0"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_8).expect("0"),
    )
  }

  @Test
  fun q10() {
    test(
      "o4-10",
      Given().expect(""),
      Given(Field.EDUCATION_EXPERIENCE, null).expect(""),
      Given(Field.EDUCATION_EXPERIENCE, Value.POSITIVE).expect("0"),
      Given(Field.EDUCATION_EXPERIENCE, Value.MOSTLY_POSITIVE).expect("0"),
      Given(Field.EDUCATION_EXPERIENCE, Value.POSITIVE_AND_NEGATIVE).expect("1"),
      Given(Field.EDUCATION_EXPERIENCE, Value.MOSTLY_NEGATIVE).expect("2"),
      Given(Field.EDUCATION_EXPERIENCE, Value.NEGATIVE).expect("2"),
    )
  }

  @Test
  fun q94() {
    test(
      "o4-94",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area not linked to serious harm notes - 
          Area not linked to reoffending notes - 
          """.trimIndent(),
        ),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
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
  fun q96() {
    test(
      "o4-96",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q98() {
    test(
      "o4-98",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o4_SAN_STRENGTH",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .expect("YES"),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .expect("NO"),
    )
  }

  @Test
  fun qNotRelatedToRisk() {
    test(
      "o4_SAN_NOT_REL_RISK",
      Given().expect(""),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.YES)
        .expect("YES"),
      Given(Field.EMPLOYMENT_EDUCATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.NO)
        .expect("NO"),
    )
  }

  @Test
  fun qSC2() {
    test(
      "oSC2",
      Given().expect(""),
      Given(Field.EDUCATION_PROFESSIONAL_OR_VOCATIONAL_QUALIFICATIONS, Value.YES)
        .expect("YES"),
      Given(Field.EDUCATION_PROFESSIONAL_OR_VOCATIONAL_QUALIFICATIONS, Value.NO)
        .expect("NO"),
    )
  }

  @Test
  fun qSC2t() {
    test(
      "oSC2-t",
      Given().expect(""),
      Given(Field.EDUCATION_PROFESSIONAL_OR_VOCATIONAL_QUALIFICATIONS_YES_DETAILS, "Some details")
        .expect("Some details"),
    )
  }

  @Test
  fun qSC3() {
    test(
      "oSC3",
      Given().expect(""),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.NONE_OF_THESE).expect("NOQUAL"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_1).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_2).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_3).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_4).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_5).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_6).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_7).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.LEVEL_8).expect("MATHSENGLISH"),
      Given(Field.EDUCATION_HIGHEST_LEVEL_COMPLETED, Value.ENTRY_LEVEL).expect("ANYOTHER"),
    )
  }

  @Test
  fun qSC4() {
    test(
      "oSC4",
      Given().expect(""),
      Given(Field.EMPLOYMENT_STATUS, Value.RETIRED).expect("FULLTIME"),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED)
        .and(Field.EMPLOYMENT_TYPE, Value.FULL_TIME)
        .expect("FULLTIME"),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED)
        .and(Field.EMPLOYMENT_TYPE, Value.PART_TIME)
        .expect("PARTTIME"),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED)
        .and(Field.EMPLOYMENT_TYPE, Value.TEMPORARY_OR_CASUAL)
        .expect("PARTTIME"),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED)
        .and(Field.EMPLOYMENT_TYPE, Value.APPRENTICESHIP)
        .expect("PARTTIME"),
      Given(Field.EMPLOYMENT_STATUS, Value.CURRENTLY_UNAVAILABLE_FOR_WORK)
        .and(Field.HAS_BEEN_EMPLOYED, Value.NO)
        .expect("UNEMPLOYED"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_LOOKING_FOR_WORK)
        .and(Field.HAS_BEEN_EMPLOYED, Value.NO)
        .expect("0"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK)
        .and(Field.HAS_BEEN_EMPLOYED, Value.NO)
        .expect("0"),
    )
  }

  @Test
  fun qSC5() {
    test(
      "oSC5",
      Given().expect(""),
      Given(Field.EMPLOYMENT_STATUS, Value.EMPLOYED).expect("Yes [Score 0]"),
      Given(Field.EMPLOYMENT_STATUS, Value.SELF_EMPLOYED).expect("Yes [Score 0]"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_LOOKING_FOR_WORK).expect("No [Score 0]"),
      Given(Field.EMPLOYMENT_STATUS, Value.UNEMPLOYED_NOT_LOOKING_FOR_WORK).expect("No [Score 0]"),
    )
  }

  @Test
  fun qSC8() {
    test(
      "oSC8",
      Given().expect(""),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.GOOD).expect("YES"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_GOOD).expect("SOMETIMES"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.FAIRLY_BAD).expect("NOTCONFIDENT"),
      Given(Field.FINANCE_MONEY_MANAGEMENT, Value.BAD).expect("NOTCONFIDENT"),
    )
  }
}
