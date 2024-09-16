package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class EmotionalWellbeingTest : SectionMappingTest(EmotionalWellbeing(), "1.0") {
  @Test
  fun q1() {
    test(
      "o10-1",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_COPING_DAY_TO_DAY_LIFE, Value.YES).expect("0"),
      Given(Field.HEALTH_WELLBEING_COPING_DAY_TO_DAY_LIFE, Value.YES_SOME_DIFFICULTIES).expect("1"),
      Given(Field.HEALTH_WELLBEING_COPING_DAY_TO_DAY_LIFE, Value.NO).expect("2"),
    )
  }

  @Test
  fun q2() {
    test(
      "o10-2",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING_SEVERE).expect("2"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING).expect("1"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_IN_THE_PAST).expect("1"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO).expect("0"),
    )
  }

  @Test
  fun q4() {
    test(
      "o10-4",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_ATTITUDE_TOWARDS_SELF, Value.POSITIVE).expect("0"),
      Given(Field.HEALTH_WELLBEING_ATTITUDE_TOWARDS_SELF, Value.SOME_NEGATIVE_ASPECTS).expect("1"),
      Given(Field.HEALTH_WELLBEING_ATTITUDE_TOWARDS_SELF, Value.NEGATIVE).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o10-5",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_SELF_HARMED, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_SELF_HARMED, Value.NO)
        .and(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS, Value.YES)
        .expect("YES"),
      Given(Field.HEALTH_WELLBEING_SELF_HARMED, Value.YES)
        .and(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS, Value.NO)
        .expect("YES"),
      Given(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_SELF_HARMED, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_SELF_HARMED, Value.NO)
        .and(Field.HEALTH_WELLBEING_ATTEMPTED_SUICIDE_OR_SUICIDAL_THOUGHTS, Value.NO)
        .expect("NO"),
    )
  }

  @Test
  fun q6() {
    test(
      "o10-6",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING_SEVERE).expect("2"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING).expect("1"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_IN_THE_PAST).expect("1"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO).expect("0"),
    )
  }

  @Test
  fun qChildhoodBehaviouralProblems() {
    test(
      "o10-7_V2_CHILDHOOD",
      Given().expect(null),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD_BEHAVIOUR, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD_BEHAVIOUR, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qHistoryOfHeadInjury() {
    test(
      "o10-7_V2_HISTHEADINJ",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_HEAD_INJURY_OR_ILLNESS, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_HEAD_INJURY_OR_ILLNESS, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qHistoryOfPsychTreatment() {
    test(
      "o10-7_V2_HISTPSYCH",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING_SEVERE).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_IN_THE_PAST).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qCurrentPsychTreatment() {
    test(
      "o10-7_V2_PSYCHTREAT",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT, Value.PENDING_TREATMENT).expect("YES"),
      Given(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT, Value.UNKNOWN).expect("NO"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.UNKNOWN).expect("NO"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO)
        .and(Field.HEALTH_WELLBEING_PSYCHIATRIC_TREATMENT, Value.YES)
        .expect("NO"),
    )
  }

  @Test
  fun q97() {
    test(
      "o10-97",
      *PractitionerAnalysisScenarios("HEALTH_WELLBEING").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o10-98",
      *PractitionerAnalysisScenarios("HEALTH_WELLBEING").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o10-99",
      *PractitionerAnalysisScenarios("HEALTH_WELLBEING").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o10_SAN_STRENGTH",
      *PractitionerAnalysisScenarios("HEALTH_WELLBEING").strengthsOrProtectiveFactors(),
    )
  }
}
