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
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.NO).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_IMPULSIVE_BEHAVIOUR, Value.YES).expect("2"),
    )
  }

  @Test
  fun q3() {
    test(
      "o11-3",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.NO_VIOLENCE).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_VIOLENCE_CONTROLLING_BEHAVIOUR, Value.YES_VIOLENCE).expect("2"),
    )
  }

  @Test
  fun q4() {
    test(
      "o11-4",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_TEMPER_MANAGEMENT, Value.NO).expect("2"),
    )
  }

  @Test
  fun q6() {
    test(
      "o11-6",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.LIMITED_PROBLEM_SOLVING).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PROBLEM_SOLVING, Value.NO).expect("2"),
    )
  }

  @Test
  fun q7() {
    test(
      "o11-7",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_CONSEQUENCES, Value.NO).expect("2"),
    )
  }

  @Test
  fun q9() {
    test(
      "o11-9",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.YES).expect("0"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_PEOPLES_VIEWS, Value.NO).expect("2"),
    )
  }

  @Test
  fun q11() {
    test(
      "o11-11",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, null).expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.YES).expect("2"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.SOMETIMES).expect("1"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SEXUAL_PREOCCUPATION, Value.NO).expect("0"),
    )
  }

  @Test
  fun q12() {
    test(
      "o11-12",
      Given().expect(null),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_OFFENCE_RELATED_SEXUAL_INTEREST, null).expect(null),
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
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o11-98",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o11-99",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfReoffending(),
    )
  }
}
