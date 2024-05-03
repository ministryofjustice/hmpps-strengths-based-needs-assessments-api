package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class EmotionalWellbeing : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o10-1" to ::q1,
      "o10-2" to ::q2,
      "o10-4" to ::q4,
      "o10-5" to ::q5,
      "o10-6" to ::q6,
      "o10-7_V2_CHILDHOOD" to ::qChildhoodBehaviouralProblems,
      "o10-7-V2-HISTHEADINJ" to ::qHistoryOfHeadInjury,
      "o10.7_V2_HISTPSYCH" to ::qHistoryOfPsychTreatment,
      "o10.7_V2_PSYCHTREAT" to ::qCurrentPsychTreatment,
      "o10-97" to ::q97,
      "o10-98" to ::q98,
      "o10-99" to ::q99,
      "o10_SAN_STRENGTH" to ::qStrength,
      "o10_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,

    )
  }

  private fun q1(): Any {
    return ""
  }

  private fun q2(): Any {
    return ""
  }

  private fun q4(): Any {
    return ""
  }

  private fun q5(): Any {
    return ""
  }

  private fun q6(): Any {
    return ""
  }

  private fun qChildhoodBehaviouralProblems(): Any {
    return ""
  }

  private fun qHistoryOfHeadInjury(): Any {
    return ""
  }

  private fun qHistoryOfPsychTreatment(): Any {
    return ""
  }

  private fun qCurrentPsychTreatment(): Any {
    return ""
  }

  private fun q97(): Any {
    return ""
  }

  private fun q98(): Any {
    return ""
  }

  private fun q99(): Any {
    return ""
  }

  private fun qStrength(): Any {
    return ""
  }

  private fun qNotRelatedToRisk(): Any {
    return ""
  }
}
