package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class HealthTest : SectionMappingTest(Health(), "1.0") {
  @Test
  fun q1() {
    test(
      "o13-1",
      Given().expect(null),
      Given(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION, null).expect(null),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, null).expect(null),
      Given(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING_SEVERE).expect("YES"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_IN_THE_PAST).expect("YES"),
      Given(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO).expect("NO"),
      Given(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION, Value.YES)
        .and(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.NO)
        .expect("YES"),
      Given(Field.HEALTH_WELLBEING_PHYSICAL_HEALTH_CONDITION, Value.NO)
        .and(Field.HEALTH_WELLBEING_MENTAL_HEALTH_CONDITION, Value.YES_ONGOING)
        .expect("YES"),
    )
  }
}
