package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class LifestyleAssociates : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o7-2" to ::q2,
      "o7-3" to ::q3,
      "o7-4" to ::q4,
      "o7-5" to ::q5,
      "o7-97" to ::q97,
      "o7-98" to ::q98,
      "o7-99" to ::q99,
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
