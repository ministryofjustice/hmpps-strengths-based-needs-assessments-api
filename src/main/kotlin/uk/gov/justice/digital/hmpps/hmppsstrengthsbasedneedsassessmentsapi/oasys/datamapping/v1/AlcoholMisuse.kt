package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class AlcoholMisuse : SectionMapping() {
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
    return when (ap.answer(Field.ALCOHOL_USE).value) {
      ap.get(Value.YES_WITHIN_LAST_THREE_MONTHS) -> {
        var total = 0

        total = calculateFrequencyScore(total)
        total = calculateUnitsScore(total)
        calculateOasysScore(total)
      }
      ap.get(Value.NO), ap.get(Value.YES_NOT_IN_LAST_THREE_MONTHS) -> "0"
      else -> null
    }
  }

  private fun calculateOasysScore(total: Int) = when (total) {
    in 8..Int.MAX_VALUE -> "2"
    in 5..7 -> "1"
    in 0..4 -> "0"
    else -> null
  }

  private fun calculateUnitsScore(total: Int): Int {
    return when (ap.answer(Field.ALCOHOL_UNITS).value) {
      ap.get(Value.UNITS_3_TO_4) -> total + 1
      ap.get(Value.UNITS_5_TO_6) -> total + 2
      ap.get(Value.UNITS_7_TO_9) -> total + 3
      ap.get(Value.UNITS_10_OR_MORE) -> total + 4
      else -> total
    }
  }

  private fun calculateFrequencyScore(total: Int): Int {
    return when (ap.answer(Field.ALCOHOL_FREQUENCY).value) {
      ap.get(Value.MULTIPLE_TIMES_A_MONTH) -> total + 1
      ap.get(Value.LESS_THAN_4_TIMES_A_WEEK) -> total + 3
      ap.get(Value.MORE_THAN_4_TIMES_A_WEEK) -> total + 4
      else -> total
    }
  }

  private fun q1t(): Any? {
    return null
  }

  private fun q2(): Any? {
    return when(ap.answer(Field.ALCOHOL_USE).value){
      ap.get(Value.NO) -> "0"
      ap.get(Value.YES_WITHIN_LAST_THREE_MONTHS), ap.get(Value.YES_NOT_IN_LAST_THREE_MONTHS) -> {
        when (ap.answer(Field.ALCOHOL_EVIDENCE_OF_EXCESS_DRINKING).value) {
          ap.get(Value.NO_EVIDENCE) -> "0"
          ap.get(Value.YES_WITH_SOME_EVIDENCE) -> "1"
          ap.get(Value.YES_WITH_EVIDENCE) -> "2"
          else -> null
        }
      }
      else -> null
    }
  }

  private fun q97(): Any? {
    return PractitionerAnalysis("ALCOHOL", ap).notes()
  }

  private fun q98(): Any? {
    return PractitionerAnalysis("ALCOHOL", ap).riskOfSeriousHarm()
  }

  private fun q99(): Any? {
    return PractitionerAnalysis("ALCOHOL", ap).riskOfReoffending()
  }

  private fun qStrength(): Any? {
    return PractitionerAnalysis("ALCOHOL", ap).strengthsOrProtectiveFactors()
  }

  private fun qNotRelatedToRisk(): Any? {
    return PractitionerAnalysis("ALCOHOL", ap).relatedToRisk()
  }
}
