package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class OffenceAnalysisTest : SectionMappingTest(OffenceAnalysis(), "1.0") {
  @Test
  fun q1() {
    test(
      "o2-1",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE, "test").expect("test"),
    )
  }

  @Test
  fun q2Violence() {
    test(
      "o2-2_V2_ANYVIOL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.VIOLENCE_OR_COERCION)).expect("YES"),
    )
  }

  @Test
  fun q2DomesticAbuse() {
    test(
      "o2-2_V2_DOM_ABUSE",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.DOMESTIC_ABUSE)).expect("YES"),
    )
  }

  @Test
  fun q2ExcessiveViolence() {
    test(
      "o2-2_V2_EXCESSIVE",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.EXCESSIVE_OR_SADISTIC_VIOLENCE)).expect("YES"),
    )
  }

  @Test
  fun q2Sexual() {
    test(
      "o2-2_V2_SEXUAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.SEXUAL_ELEMENT)).expect("YES"),
    )
  }

  @Test
  fun q6() {
    test(
      "o2-6",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, Value.YES).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, Value.NO).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, "unknown value").expect(null),
    )
  }

  @Test
  fun q72() {
    test(
      "o2-7-2",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.PRESSURISED)).expect("YES"),
    )
  }

  @Test
  fun q8() {
    test(
      "o2-8",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_REASON, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_REASON, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_REASON, "test").expect("test"),
    )
  }

  @Test
  fun q9Sexual() {
    test(
      "o2-9_V2_SEXUAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.SEXUAL_DESIRES)).expect("YES"),
    )
  }

  @Test
  fun q9Financial() {
    test(
      "o2-9_V2_FINANCIAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.BASIC_FINANCIAL_NEEDS)).expect("YES"),
    )
  }

  @Test
  fun q9Addiction() {
    test(
      "o2-9_V2_ADDICTION",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.SUPPORTING_DRUG_USE)).expect("YES"),
    )
  }

  @Test
  fun q9Emotional() {
    test(
      "o2-9_V2_EMOTIONAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_GAIN, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_GAIN, listOf(Value.EMOTIONS_CLOUDED_JUDGEMENT)).expect("YES"),
    )
  }

  @Test
  fun q12() {
    test(
      "o2-12",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, "test").expect("test"),
    )
  }

  @Test
  fun q98() {
    test(
      "o2-98",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.YES).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.NO).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.YES)
        .and(Field.YES_OFFENCE_RISK_DETAILS, null)
        .expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.YES)
        .and(Field.YES_OFFENCE_RISK_DETAILS, "some text")
        .expect("some text"),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.NO)
        .and(Field.NO_OFFENCE_RISK_DETAILS, "some text")
        .expect("some text"),
      Given(Field.OFFENCE_ANALYSIS_RISK, "unknown value").expect(null),
    )
  }

  @Test
  fun q99() {
    test(
      "o2-99",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.YES).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_RISK, Value.NO).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_RISK, "unknown value").expect(null),
    )
  }
}
