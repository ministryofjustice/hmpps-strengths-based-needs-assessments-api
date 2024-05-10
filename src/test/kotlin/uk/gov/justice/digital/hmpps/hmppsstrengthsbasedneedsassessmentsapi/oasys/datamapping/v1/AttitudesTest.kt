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
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CRIMINAL_BEHAVIOUR, Value.YES).expect("2"),
    )
  }

  @Test
  fun q3() {
    test(
      "o12-3",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.YES_POSITIVE).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.NEGATIVE_ATTITUDE_NO_CONCERNS).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_POSITIVE_ATTITUDE, Value.NEGATIVE_ATTITUDE_AND_CONCERNS).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o12-4",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.YES_SUPERVISION).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.UNSURE_SUPERVISION).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SUPERVISION, Value.NO_SUPERVISION).expect("2"),
    )
  }

  @Test
  fun q9() {
    test(
      "o12-9",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.SOME).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_HOSTILE_ORIENTATION, Value.YES).expect("2"),
    )
  }

  @Test
  fun q97() {
    test(
      "o12-97",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o12-98",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o12-99",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfReoffending(),
    )
  }
}
