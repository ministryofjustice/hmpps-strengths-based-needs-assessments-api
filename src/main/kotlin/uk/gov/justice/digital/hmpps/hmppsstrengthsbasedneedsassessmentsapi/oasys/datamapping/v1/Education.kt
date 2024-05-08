package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Education : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o4-2" to ::q2,
      "o4-3" to ::q3,
      "o4-4" to ::q4,
      "o4-5" to ::q5,
      "o4-7" to ::q7,
      "o4-7-1" to ::q71,
      "o4-8" to ::q8,
      "o4-9" to ::q9,
      "o4-10" to ::q10,
      "o4-94" to ::q94,
      "o4-96" to ::q96,
      "o4-98" to ::q98,
      "o4_SAN_STRENGTH" to ::qStrength,
      "o4_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
      "oSC2" to ::qSC2,
      "oSC2-t" to ::qSC2t,
      "oSC3" to ::qSC3,
      "oSC4" to ::qSC4,
      "oSC5" to ::qSC5,
      "oSC8" to ::qSC6,
    )
  }

  private fun q2(): Any {
    return ""
  }
  private fun q3(): Any {
    return ""
  }
  private fun q4(): Any {
    return ""
  }
  private fun q5(): Any {
    return ""
  }
  private fun q7(): Any {
    return ""
  }
  private fun q71(): Any {
    return ""
  }
  private fun q8(): Any {
    return ""
  }
  private fun q9(): Any {
    return ""
  }
  private fun q10(): Any {
    return ""
  }
  private fun q94(): Any {
    return ""
  }
  private fun q96(): Any {
    return ""
  }
  private fun q98(): Any {
    return ""
  }
  private fun qStrength(): Any {
    return ""
  }
  private fun qNotRelatedToRisk(): Any {
    return ""
  }
  private fun qSC2(): Any {
    return ""
  }
  private fun qSC2t(): Any {
    return ""
  }
  private fun qSC3(): Any {
    return ""
  }
  private fun qSC4(): Any {
    return ""
  }
  private fun qSC5(): Any {
    return ""
  }
  private fun qSC6(): Any {
    return ""
  }
}
