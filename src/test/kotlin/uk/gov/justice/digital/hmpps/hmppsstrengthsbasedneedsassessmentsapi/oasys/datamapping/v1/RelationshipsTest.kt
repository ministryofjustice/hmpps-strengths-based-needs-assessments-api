package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class RelationshipsTest : SectionMappingTest(Relationships(), "1.0") {
  @Test
  fun q1() {
    test(
      "o6-1",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_FAMILY_RELATIONSHIP, Value.UNSTABLE_RELATIONSHIP).expect("2"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_FAMILY_RELATIONSHIP, Value.MIXED_RELATIONSHIP).expect("0"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_FAMILY_RELATIONSHIP, Value.STABLE_RELATIONSHIP).expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_FAMILY_RELATIONSHIP, Value.UNKNOWN).expect(""),
    )
  }

  @Test
  fun q3() {
    test(
      "o6-3",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD, Value.POSITIVE_CHILDHOOD).expect("0"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD, Value.MIXED_CHILDHOOD).expect("1"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD, Value.NEGATIVE_CHILDHOOD).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o6-4",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CURRENT_RELATIONSHIP, Value.HAPPY_RELATIONSHIP).expect("0"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CURRENT_RELATIONSHIP, Value.CONCERNS_HAPPY_RELATIONSHIP).expect("1"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CURRENT_RELATIONSHIP, Value.UNHAPPY_RELATIONSHIP).expect("2"),
    )
  }

  @Test
  fun q6() {
    test(
      "o6-6",
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_INTIMATE_RELATIONSHIP, Value.STABLE_RELATIONSHIPS).expect("0"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_INTIMATE_RELATIONSHIP, Value.POSITIVE_AND_NEGATIVE_RELATIONSHIPS).expect("1"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_INTIMATE_RELATIONSHIP, Value.UNSTABLE_RELATIONSHIPS).expect("2"),
    )
  }

  @Test
  fun q9() {
    test(
      "o6-9",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_IMPORTANT_PEOPLE, emptyList()).expect("NO"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_IMPORTANT_PEOPLE, listOf(Value.CHILD_PARENTAL_RESPONSIBILITIES)).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_IMPORTANT_PEOPLE, listOf(Value.OTHER, Value.CHILD_PARENTAL_RESPONSIBILITIES))
        .expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_IMPORTANT_PEOPLE, listOf(Value.FAMILY)).expect("NO"),
    )
  }

  @Test
  fun q10() {
    listOf("").withIndex()
    test(
      "o6-10",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PARENTAL_RESPONSIBILITIES, Value.YES).expect("Significantproblems"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PARENTAL_RESPONSIBILITIES, Value.SOMETIMES).expect("Someproblems"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PARENTAL_RESPONSIBILITIES, Value.NO).expect("Noproblems"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PARENTAL_RESPONSIBILITIES, Value.UNKNOWN).expect(""),
    )
  }

  @Test
  fun q11() {
    test(
      "o6-11",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_RISK_SEXUAL_HARM, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_RISK_SEXUAL_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q12() {
    test(
      "o6-12",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_EMOTIONAL_INTIMACY, Value.YES).expect("2"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_EMOTIONAL_INTIMACY, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_EMOTIONAL_INTIMACY, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_EMOTIONAL_INTIMACY, Value.UNKNOWN).expect(""),
    )
  }

  @Test
  fun q97() {
    test(
      "o6-97",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area not linked to serious harm notes - 
          Area not linked to reoffending notes - 
          """.trimIndent(),
        ),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
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
      "o6-98",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q99() {
    test(
      "o6-99",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o6_SAN_STRENGTH",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qNotRelatedToRisk() {
    test(
      "o6_SAN_NOT_REL_RISK",
      Given().expect(""),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.NO).expect("NO"),
    )
  }
}
