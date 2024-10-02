package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class NewSectionsTest : SectionMappingTest(NewSections(), "1.0") {
  @Test
  fun q30() {
    test(
      "o1-30",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, null)
        .and(Field.OFFENCE_ANALYSIS_MOTIVATIONS, null)
        .expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, Value.SEXUAL_ELEMENT).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, Value.SEXUAL_ELEMENT)
        .and(Field.OFFENCE_ANALYSIS_MOTIVATIONS, null)
        .expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, Value.SEXUAL_MOTIVATION).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, Value.SEXUAL_MOTIVATION)
        .and(Field.OFFENCE_ANALYSIS_ELEMENTS, null)
        .expect("YES"),
    )
  }

  @Test
  fun qLinkedToRosh() {
    test(
      "oTBA_SAN_LINKED_ROSH",
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfSeriousHarm(),
    )
  }

  @Test
  fun qLinkedToReoffending() {
    test(
      "oTBA_SAN_LINKED_REOFFEND",
      Given().expect(null),
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "oTBA_SAN_STRENGTH",
      Given().expect(null),
      *PractitionerAnalysisScenarios("THINKING_BEHAVIOURS_ATTITUDES").strengthsOrProtectiveFactors(),
    )
  }

  @Test
  fun qAccommodationComplete() {
    test(
      "oAC_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.ACCOMMODATION_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.ACCOMMODATION_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qEducationEmploymentComplete() {
    test(
      "oEE_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.EMPLOYMENT_EDUCATION_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.EMPLOYMENT_EDUCATION_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qFinanceComplete() {
    test(
      "oFI_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.FINANCE_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.FINANCE_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qDrugsComplete() {
    test(
      "oSMD_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.DRUG_USE_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.DRUG_USE_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qAlcoholCompletes() {
    test(
      "oSMA_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.ALCOHOL_USE_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.ALCOHOL_USE_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qHealthWellbeingComplete() {
    test(
      "oHW_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.HEALTH_WELLBEING_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.HEALTH_WELLBEING_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qPersonalRelationshipsComplete() {
    test(
      "oPRC_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qThinkingBehavioursAttitudesComplete() {
    test(
      "oTBA_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.THINKING_BEHAVIOURS_ATTITUDES_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun qOffenceAnalysisComplete() {
    test(
      "oOA_SAN_SECTION_COMP",
      Given().expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_SECTION_COMPLETE, Value.YES).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_SECTION_COMPLETE, Value.NO).expect("NO"),
    )
  }
}
