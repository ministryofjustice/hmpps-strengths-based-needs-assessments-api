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
      Given().expect(null),
      Given(Field.CURRENT_ACCOMMODATION, null).expect(null),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect("NO"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect("NO"),
    )
  }

  @Test
  fun q4() {
    test(
      "o3-4",
      Given().expect(null),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING, Value.YES).expect("2"),
      Given(Field.SUITABLE_HOUSING, null).expect(null),
      Given(Field.SUITABLE_HOUSING, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING, Value.YES_WITH_CONCERNS).expect("1"),
      Given(Field.SUITABLE_HOUSING, Value.NO).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o3-5",
      Given().expect(null),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY)
        .and(Field.TYPE_OF_TEMPORARY_ACCOMMODATION, Value.SHORT_TERM).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect("0"),
    )
  }

  @Test
  fun q6() {
    test(
      "o3-6",
      Given().expect(null),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.SUITABLE_HOUSING_LOCATION, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING_LOCATION, Value.NO).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING_LOCATION, Value.YES).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect(null),
    )
  }

  @Test
  fun q97() {
    test(
      "o3-97",
      *PractitionerAnalysisScenarios("ACCOMMODATION").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o3-98",
      *PractitionerAnalysisScenarios("ACCOMMODATION").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o3-99",
      *PractitionerAnalysisScenarios("ACCOMMODATION").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o3_SAN_STRENGTH",
      *PractitionerAnalysisScenarios("ACCOMMODATION").strengthsOrProtectiveFactors(),
    )
  }
}
