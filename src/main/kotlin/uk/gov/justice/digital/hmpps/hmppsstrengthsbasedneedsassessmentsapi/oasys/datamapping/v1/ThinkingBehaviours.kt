package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class ThinkingBehaviours : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o11-2" to ::q2,
      "o11-3" to ::q3,
      "o11-4" to ::q4,
      "o11-6" to ::q6,
      "o11-7" to ::q7,
      "o11-9" to ::q9,
      "o11-11" to ::q11,
      "o11-12" to ::q12,
      "o11-97" to ::q97,
      "o11-98" to ::q98,
      "o11-99" to ::q99,
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

  private fun q6(): Any {
    return ""
  }

  private fun q7(): Any {
    return ""
  }

  private fun q9(): Any {
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
}
