package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class AccommodationTest : SectionMappingTest(Accommodation(), "1.0") {
  @Test
  fun q3() {
    test(
      "o3-3",
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, null).expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect("NO"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect("NO"),
    )
  }

  @Test
  fun q4() {
    test(
      "o3-4",
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING, Value.YES).expect("2"),
      Given(Field.SUITABLE_HOUSING, null).expect(""),
      Given(Field.SUITABLE_HOUSING, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING, Value.YES_WITH_CONCERNS).expect("1"),
      Given(Field.SUITABLE_HOUSING, Value.NO).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o3-5",
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY)
        .and(Field.TYPE_OF_TEMPORARY_ACCOMMODATION, Value.SHORT_TERM).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect(""),
    )
  }

  @Test
  fun q6() {
    test(
      "o3-6",
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.SUITABLE_HOUSING_LOCATION, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING_LOCATION, Value.NO).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING_LOCATION, Value.YES).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect(""),
    )
  }

  @Test
  fun q97() {
    test(
      "o3-97",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .expect(
          """
          Area not linked to strengths and positive factors notes - 
          Area not linked to serious harm notes - 
          Area not linked to reoffending notes - 
          """.trimIndent(),
        ),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_DETAILS, "Details 1 go here")
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_DETAILS, "Details 2 go here")
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES)
        .and(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_DETAILS, "Details 3 go here")
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
      "o3-98",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.YES).expect("YES"),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q99() {
    test(
      "o3-99",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.YES).expect("YES"),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o3_SAN_STRENGTH",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.YES).expect("YES"),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qNotRelatedToRisk() {
    test(
      "o3_SAN_NOT_REL_RISK",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.YES).expect("YES"),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RELATED_TO_RISK, Value.NO).expect("NO"),
    )
  }
}
