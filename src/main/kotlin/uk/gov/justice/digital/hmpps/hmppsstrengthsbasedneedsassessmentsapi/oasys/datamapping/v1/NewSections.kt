package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class NewSections : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap = mapOf(
    "o1-30" to ::q30,
    "oTBA_SAN_LINKED_ROSH" to ::qLinkedToRosh,
    "oTBA_SAN_LINKED_REOFFEND" to ::qLinkedToReoffending,
    "oTBA_SAN_STRENGTH" to ::qStrength,
    "oAC_SAN_SECTION_COMP" to ::qAccommodationComplete,
    "oEE_SAN_SECTION_COMP" to ::qEducationEmploymentComplete,
    "oFI_SAN_SECTION_COMP" to ::qFinanceComplete,
    "oSMD_SAN_SECTION_COMP" to ::qDrugsComplete,
    "oSMA_SAN_SECTION_COMP" to ::qAlcoholCompletes,
    "oHW_SAN_SECTION_COMP" to ::qHealthWellbeingComplete,
    "oPRC_SAN_SECTION_COMP" to ::qPersonalRelationshipsComplete,
    "oTBA_SAN_SECTION_COMP" to ::qThinkingBehavioursAttitudesComplete,
    "oOA_SAN_SECTION_COMP" to ::qOffenceAnalysisComplete,
  )

  private fun q30(): Any? {
    val sexualElements = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values.orEmpty().contains(ap.get(Value.SEXUAL_ELEMENT))
    val sexualMotivation = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values.orEmpty().contains(ap.get(Value.SEXUAL_MOTIVATION))

    return if (sexualElements || sexualMotivation) "YES" else null
  }

  private fun qLinkedToRosh(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfSeriousHarm()

  private fun qLinkedToReoffending(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).riskOfReoffending()

  private fun qStrength(): Any? = PractitionerAnalysis("THINKING_BEHAVIOURS_ATTITUDES", ap).strengthsOrProtectiveFactors()

  private fun qAccommodationComplete(): Any = if (ap.answer(Field.ACCOMMODATION_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qEducationEmploymentComplete(): Any = if (ap.answer(Field.EMPLOYMENT_EDUCATION_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qFinanceComplete(): Any = if (ap.answer(Field.FINANCE_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qDrugsComplete(): Any = if (ap.answer(Field.DRUG_USE_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qAlcoholCompletes(): Any = if (ap.answer(Field.ALCOHOL_USE_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qHealthWellbeingComplete(): Any = if (ap.answer(Field.HEALTH_WELLBEING_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qPersonalRelationshipsComplete(): Any = if (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qThinkingBehavioursAttitudesComplete(): Any = if (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_SECTION_COMPLETE).value == ap.get(Value.YES)) {
    "YES"
  } else {
    "NO"
  }

  private fun qOffenceAnalysisComplete(): Any = if (ap.answer(Field.OFFENCE_ANALYSIS_SECTION_COMPLETE).value == ap.get(Value.YES)) "YES" else "NO"
}
