package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class PredictorsTest : SectionMappingTest(Predictors(), "1.0") {
  @Test
  fun q30() {
    test(
      "o1-30",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList())
        .and(Field.OFFENCE_ANALYSIS_GAIN, emptyList())
        .expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.SEXUAL_ELEMENT)).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.SEXUAL_DESIRES)).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList())
        .and(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.SEXUAL_DESIRES))
        .expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.SEXUAL_ELEMENT))
        .and(Field.OFFENCE_ANALYSIS_GAIN, emptyList())
        .expect("YES"),
    )
  }
}
