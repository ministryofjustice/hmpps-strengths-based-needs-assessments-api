package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value

class PractitionerAnalysisScenarios(private val sectionPrefix: String) {
  fun notes(): Array<Given> {
    return arrayOf(
      Given().expect(null),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.YES)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_YES_DETAILS"), "Details 2 go here")
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.YES)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_YES_DETAILS"), "Details 1 go here")
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.YES)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_YES_DETAILS"), "Details 3 go here")
        .expect(
          """
          Strengths and protective factor notes - Details 1 go here
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.NO)
        .expect(null),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_NO_DETAILS"), "Details 2 go here")
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS_NO_DETAILS"), "Details 1 go here")
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_NO_DETAILS"), "Details 3 go here")
        .expect(
          """
          Area not linked to strengths and positive factors notes - Details 1 go here
          Area not linked to serious harm notes - Details 2 go here
          Area not linked to reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.YES)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM_YES_DETAILS"), "Details 2 go here")
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.NO)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.YES)
        .and(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING_YES_DETAILS"), "Details 3 go here")
        .expect(
          """
          Area linked to serious harm notes - Details 2 go here
          Risk of reoffending notes - Details 3 go here
          """.trimIndent(),
        ),
    )
  }

  fun riskOfSeriousHarm(): Array<Given> {
    return arrayOf(
      Given().expect(null),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.YES).expect("YES"),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM"), Value.NO).expect("NO"),
    )
  }

  fun riskOfReoffending(): Array<Given> {
    return arrayOf(
      Given().expect(null),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.YES).expect("YES"),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING"), Value.NO).expect("NO"),
    )
  }

  fun strengthsOrProtectiveFactors(): Array<Given> {
    return arrayOf(
      Given().expect(null),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.YES).expect("YES"),
      Given(Field.valueOf(sectionPrefix + "_PRACTITIONER_ANALYSIS_STRENGTHS_OR_PROTECTIVE_FACTORS"), Value.NO).expect("NO"),
    )
  }
}
