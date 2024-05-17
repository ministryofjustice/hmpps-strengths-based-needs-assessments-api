package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class AlcoholMisuseTest : SectionMappingTest(AlcoholMisuse(), "1.0") {
  @Test
  fun q1() {
    test(
      "o9-1",
      Given().expect(null),
      Given(Field.ALCOHOL_USE, null).expect(null),
      Given(Field.ALCOHOL_USE, Value.NO).expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_NOT_IN_LAST_THREE_MONTHS).expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("2"),
    )
  }

  @Test
  fun q1t() {
    test(
      "o9-1-t",
      Given().expect(null),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("Only drinks once a month or less and consumes 1 to 2 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("Only drinks once a month or less and consumes 3 to 4 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("Only drinks once a month or less and consumes 5 to 6 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("Only drinks once a month or less and consumes 7 to 9 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.ONCE_A_MONTH_OR_LESS)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("Only drinks once a month or less and consumes 10 or more units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("Drinks multiple times a month and consumes 1 to 2 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("Drinks multiple times a month and consumes 3 to 4 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("Drinks multiple times a month and consumes 5 to 6 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("Drinks multiple times a month and consumes 7 to 9 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MULTIPLE_TIMES_A_MONTH)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("Drinks multiple times a month and consumes 10 or more units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("Drinks less than four times a week and consumes 1 to 2 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("Drinks less than four times a week and consumes 3 to 4 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("Drinks less than four times a week and consumes 5 to 6 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("Drinks less than four times a week and consumes 7 to 9 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.LESS_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("Drinks less than four times a week and consumes 10 or more units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_1_TO_2)
        .expect("Drinks more than four times a week and consumes 1 to 2 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_3_TO_4)
        .expect("Drinks more than four times a week and consumes 3 to 4 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_5_TO_6)
        .expect("Drinks more than four times a week and consumes 5 to 6 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_7_TO_9)
        .expect("Drinks more than four times a week and consumes 7 to 9 units a day, when they drink."),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_FREQUENCY, Value.MORE_THAN_4_TIMES_A_WEEK)
        .and(Field.ALCOHOL_UNITS, Value.UNITS_10_OR_MORE)
        .expect("Drinks more than four times a week and consumes 10 or more units a day, when they drink."),
    )
  }

  @Test
  fun q2() {
    test(
      "o9-2",
      Given().expect(null),
      Given(Field.ALCOHOL_USE, null).expect(null),
      Given(Field.ALCOHOL_USE, Value.NO).expect(null),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.NO_EVIDENCE)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_NOT_IN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.NO_EVIDENCE)
        .expect("0"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.YES_WITH_SOME_EVIDENCE)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_NOT_IN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.YES_WITH_SOME_EVIDENCE)
        .expect("1"),
      Given(Field.ALCOHOL_USE, Value.YES_WITHIN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.YES_WITH_EVIDENCE)
        .expect("2"),
      Given(Field.ALCOHOL_USE, Value.YES_NOT_IN_LAST_THREE_MONTHS)
        .and(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING, Value.YES_WITH_EVIDENCE)
        .expect("2"),
    )
  }

  @Test
  fun q97() {
    test(
      "o9-97",
      *PractitionerAnalysisScenarios("ALCOHOL").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o9-98",
      *PractitionerAnalysisScenarios("ALCOHOL").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o9-99",
      *PractitionerAnalysisScenarios("ALCOHOL").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o9_SAN_STRENGTH",
      *PractitionerAnalysisScenarios("ALCOHOL").strengthsOrProtectiveFactors(),
    )
  }

  @Test
  fun qNotRelatedToRisk() {
    test(
      "o9_SAN_NOT_REL_RISK",
      *PractitionerAnalysisScenarios("ALCOHOL").relatedToRisk(),
    )
  }
}
