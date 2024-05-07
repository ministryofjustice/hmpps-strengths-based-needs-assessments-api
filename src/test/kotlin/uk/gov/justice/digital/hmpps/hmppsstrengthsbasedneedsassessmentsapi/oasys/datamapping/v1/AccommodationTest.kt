package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Scenarios
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class AccommodationTest {
  private val sut = Accommodation()

  @Test
  fun q3() {
    Scenarios(
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, null).expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.TEMPORARY).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("YES"),
      Given(Field.CURRENT_ACCOMMODATION, Value.SETTLED).expect("NO"),
    ).test(sut, "o3-3")
  }

  @Test
  fun q4() {
    Scenarios(
      Given().expect(""),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION).expect("2"),
      Given(Field.CURRENT_ACCOMMODATION, Value.NO_ACCOMMODATION)
        .and(Field.SUITABLE_HOUSING, Value.YES).expect("2"),
      Given(Field.SUITABLE_HOUSING, null).expect(""),
      Given(Field.SUITABLE_HOUSING, Value.YES).expect("0"),
      Given(Field.SUITABLE_HOUSING, Value.YES_WITH_CONCERNS).expect("1"),
      Given(Field.SUITABLE_HOUSING, Value.NO).expect("2"),
    ).test(sut, "o3-4")
  }

  @Test
  fun q98() {
    Scenarios(
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, null).expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, "").expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM, "test").expect("test"),
    ).test(sut, "o3-98")
  }

  @Test
  fun q99() {
    Scenarios(
      Given().expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, null).expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, "").expect(""),
      Given(Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING, "test").expect("test"),
    ).test(sut, "o3-99")
  }
}
