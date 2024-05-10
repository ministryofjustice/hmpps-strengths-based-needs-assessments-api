package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class LifestyleAssociatesTest : SectionMappingTest(LifestyleAssociates(), "1.0") {
  @Test
  fun q2() {
    test(
      "o7-2",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENDING_ACTIVITIES, null).expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENDING_ACTIVITIES, Value.NO_OFFENDING_ACTIVITIES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENDING_ACTIVITIES, Value.SOMETIMES_OFFENDING_ACTIVITIES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENDING_ACTIVITIES, Value.YES_OFFENDING_ACTIVITIES).expect("2"),
    )
  }

  @Test
  fun q3() {
    test(
      "o7-3",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEER_PRESSURE, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEER_PRESSURE, Value.SOME).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEER_PRESSURE, Value.NO).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o7-4",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_MANIPULATIVE_PREDATORY_BEHAVIOUR, Value.YES).expect("2"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_MANIPULATIVE_PREDATORY_BEHAVIOUR, Value.SOME).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_MANIPULATIVE_PREDATORY_BEHAVIOUR, Value.NO).expect("0"),
    )
  }

  @Test
  fun q5() {
    test(
      "o7-5",
      Given().expect(""),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_STABLE_BEHAVIOUR, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_STABLE_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_STABLE_BEHAVIOUR, Value.NO).expect("2"),
    )
  }
}
