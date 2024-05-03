package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Relationships : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o6-1" to ::q1,
      "o6-3" to ::q3,
      "o6-4" to ::q4,
      "o6-6" to ::q6,
      "o6-9" to ::q9,
      "o6-10" to ::q10,
      "o6-11" to ::q11,
      "o6-12" to ::q12,
      "o6-97" to ::q97,
      "o6-98" to ::q98,
      "o6-99" to ::q99,
      "o6_SAN_STRENGTH" to ::qStrength,
      "o6_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
  }

  private fun q1(): Any {
    return ""
  }

  private fun q3(): Any {
    return ""
  }

  private fun q4(): Any {
    return ""
  }

  private fun q6(): Any {
    return ""
  }

  private fun q9(): Any {
    return ""
  }

  private fun q10(): Any {
    return ""
  }

  private fun q11(): Any {
    return ""
  }

  private fun q12(): Any {
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
