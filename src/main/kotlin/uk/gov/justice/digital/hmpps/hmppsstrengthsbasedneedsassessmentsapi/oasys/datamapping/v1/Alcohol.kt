package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Alcohol : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o9-1" to ::q1,
      "o9-1-t" to ::q1t,
      "o9-2" to ::q2,
      "o9-97" to ::q97,
      "o9-98" to ::q98,
      "o9-99" to ::q99,
      "o9_SAN_STRENGTH" to ::qStrength,
      "o9_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
  }

  private fun q1(): Any? {
    return null
  }

  private fun q1t(): Any? {
    return null
  }

  private fun q2(): Any? {
    return null
  }

  private fun q97(): Any? {
    return null
  }

  private fun q98(): Any? {
    return null
  }

  private fun q99(): Any? {
    return null
  }

  private fun qStrength(): Any? {
    return null
  }

  private fun qNotRelatedToRisk(): Any? {
    return null
  }
}
