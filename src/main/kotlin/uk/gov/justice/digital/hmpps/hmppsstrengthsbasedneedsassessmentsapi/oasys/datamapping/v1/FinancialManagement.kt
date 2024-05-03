package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class FinancialManagement : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o5-3" to ::q3,
      "o5-4" to ::q4,
      "o5-5" to ::q5,
      "o5-6" to ::q6,
      "o5-97" to ::q97,
      "o5-98" to ::q98,
      "o5-99" to ::q99,
      "o5_SAN_STRENGTH" to ::qStrength,
      "o5_SAN_NOT_REL_RISK" to ::qNotRelatedToRisk,
    )
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

  private fun q6(): Any {
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
