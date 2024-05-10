package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent

typealias FieldsToMap = Map<String, () -> Any?>

abstract class SectionMapping {
  protected lateinit var ap: AnswersProvider

  abstract fun getFieldsToMap(): FieldsToMap

  fun map(answersProvider: AnswersProvider): OasysEquivalent {
    ap = answersProvider
    val result = mutableMapOf<String, Any?>()
    for ((field, method) in getFieldsToMap()) {
      result[field] = method()
    }
    return result
  }
}
