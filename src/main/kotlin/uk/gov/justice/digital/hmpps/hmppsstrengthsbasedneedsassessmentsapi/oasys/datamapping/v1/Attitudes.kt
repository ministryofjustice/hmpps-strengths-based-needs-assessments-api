package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Attitudes : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o12-1" to ::q1,
      "o12-3" to ::q3,
      "o12-4" to ::q4,
      "o12-9" to ::q9,
      "o12-97" to ::q97,
      "o12-98" to ::q98,
      "o12-99" to ::q99,
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

  private fun q9(): Any {
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
