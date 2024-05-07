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
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, null).expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect("NO"),
    )
  }

  @Test
  fun q4() {
    test(
      "o3-4",
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING, Value.YES).expect("2"),
      Given(Field.SUITABLE_HOUSING, null).expect(""),
      Given(Field.SUITABLE_HOUSING, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING, Value.YES_WITH_CONCERNS).expect("1"),
      Given(Field.SUITABLE_HOUSING, Value.NO).expect("2"),
    )
  }

  @Test
  fun q98() {
    test(
      "o3-98",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, null).expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, "").expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, "test").expect("test"),
    )
  }

  @Test
  fun q99() {
    test(
      "o3-99",
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, null).expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, "").expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, "test").expect("test"),
    )
  }
}
