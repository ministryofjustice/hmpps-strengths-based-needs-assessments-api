package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class NewSections : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "oTBA_SAN_LINKED_ROSH" to ::qLinkedToRosh,
      "oTBA_SAN_LINKED_REOFFEND" to ::qLinkedToReoffending,
      "oTBA_SAN_STRENGTH" to ::qStrength,
      "oTBA_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
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
  }

  private fun qLinkedToRosh(): Any? {
    TODO()
  }

  private fun qLinkedToReoffending(): Any? {
    TODO()
  }

  private fun qStrength(): Any? {
    TODO()
  }

  private fun qNotRelatedToRisk(): Any? {
    TODO()
  }

  private fun qAccommodationComplete(): Any? {
    TODO()
  }

  private fun qEducationEmploymentComplete(): Any? {
    TODO()
  }

  private fun qFinanceComplete(): Any? {
    TODO()
  }

  private fun qDrugsComplete(): Any? {
    TODO()
  }

  private fun qAlcoholCompletes(): Any? {
    TODO()
  }

  private fun qHealthWellbeingComplete(): Any? {
    TODO()
  }

  private fun qPersonalRelationshipsComplete(): Any? {
    TODO()
  }

  private fun qThinkingBehavioursAttitudesComplete(): Any? {
    TODO()
  }

  private fun qOffenceAnalysisComplete(): Any? {
    TODO()
  }
}
