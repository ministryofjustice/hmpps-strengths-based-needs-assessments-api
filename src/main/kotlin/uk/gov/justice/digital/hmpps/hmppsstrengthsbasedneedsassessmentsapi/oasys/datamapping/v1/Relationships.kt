package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Relationships : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o6-1" to ::q1,
      "o6-3" to ::q3,
      "o6-4" to ::q4,
      "o6-6" to ::q6,
      "o6-7-1-1da" to ::q711da,
      "o6-7-1-2da" to ::q712da,
      "o6-7-2-1da" to ::q721da,
      "o6-7-2-2da" to ::q722da,
      "o6-9" to ::q9,
      "o6-10" to ::q10,
      "o6-11" to ::q11,
      "o6-12" to ::q12,
      "o6-97" to ::q97,
      "o6-98" to ::q98,
      "o6-99" to ::q99,
      "o6_SAN_STRENGTH" to ::qStrength,
    )
  }

  private fun q1(): Any? {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_FAMILY_RELATIONSHIP).value) {
      ap.get(Value.UNSTABLE_RELATIONSHIP) -> "2"
      ap.get(Value.MIXED_RELATIONSHIP) -> "1"
      ap.get(Value.STABLE_RELATIONSHIP) -> "0"
      else -> null
    }
  }

  private fun q3(): Any? {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CHILDHOOD).value) {
      ap.get(Value.POSITIVE_CHILDHOOD) -> "0"
      ap.get(Value.MIXED_CHILDHOOD) -> "1"
      ap.get(Value.NEGATIVE_CHILDHOOD) -> "2"
      else -> null
    }
  }

  private fun q4(): Any? {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_CURRENT_RELATIONSHIP).value) {
      ap.get(Value.HAPPY_RELATIONSHIP) -> "0"
      ap.get(Value.CONCERNS_HAPPY_RELATIONSHIP) -> "1"
      ap.get(Value.UNHAPPY_RELATIONSHIP) -> "2"
      else -> null
    }
  }

  private fun q6(): Any? {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_INTIMATE_RELATIONSHIP).value) {
      ap.get(Value.STABLE_RELATIONSHIPS) -> "0"
      ap.get(Value.POSITIVE_AND_NEGATIVE_RELATIONSHIPS) -> "1"
      ap.get(Value.UNSTABLE_RELATIONSHIPS) -> "2"
      else -> null
    }
  }

  private fun q711da(): Any? {
    return null
//    return when (ap.answer(Field.OFFENCE_ANALYSIS_VICTIM_OF_DOMESTIC_ABUSE_TYPE).value) {
//      ap.get(Value.INTIMATE_PARTNER),
//      ap.get(Value.FAMILY_MEMBER_AND_INTIMATE_PARTNER),
//      -> "YES"
//      else -> null
//    }
  }

  private fun q712da(): Any? {
    return null
//    return when (ap.answer(Field.OFFENCE_ANALYSIS_VICTIM_OF_DOMESTIC_ABUSE_TYPE).value) {
//      ap.get(Value.FAMILY_MEMBER),
//      ap.get(Value.FAMILY_MEMBER_AND_INTIMATE_PARTNER),
//      -> "YES"
//      else -> null
//    }
  }

  private fun q721da(): Any? {
    return null
//    return when (ap.answer(Field.OFFENCE_ANALYSIS_PERPETRATOR_OF_DOMESTIC_ABUSE_TYPE).value) {
//      ap.get(Value.INTIMATE_PARTNER),
//      ap.get(Value.FAMILY_MEMBER_AND_INTIMATE_PARTNER),
//      -> "YES"
//      else -> null
//    }
  }

  private fun q722da(): Any? {
    return null
//    return when (ap.answer(Field.OFFENCE_ANALYSIS_PERPETRATOR_OF_DOMESTIC_ABUSE_TYPE).value) {
//      ap.get(Value.FAMILY_MEMBER),
//      ap.get(Value.FAMILY_MEMBER_AND_INTIMATE_PARTNER),
//      -> "YES"
//      else -> null
//    }
  }

  private fun q9(): Any? {
    val answer = ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_IMPORTANT_PEOPLE).values
    return when (answer?.contains(ap.get(Value.CHILD_PARENTAL_RESPONSIBILITIES))) {
      true -> "YES"
      false -> "NO"
      else -> null
    }
  }

  private fun q10(): Any? {
    return when (ap.answer(Field.PERSONAL_RELATIONSHIPS_COMMUNITY_PARENTAL_RESPONSIBILITIES).value) {
      ap.get(Value.NO) -> "Significantproblems"
      ap.get(Value.SOMETIMES) -> "Someproblems"
      ap.get(Value.YES) -> "Noproblems"
      else -> null
    }
  }

  private fun q11(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_RISK_SEXUAL_HARM).value) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  private fun q12(): Any? {
    return when (ap.answer(Field.THINKING_BEHAVIOURS_ATTITUDES_EMOTIONAL_INTIMACY).value) {
      ap.get(Value.YES) -> "2"
      ap.get(Value.SOMETIMES) -> "1"
      ap.get(Value.NO) -> "0"
      else -> null
    }
  }

  private fun q97(): Any? {
    return PractitionerAnalysis("PERSONAL_RELATIONSHIPS_COMMUNITY", ap).notes()
  }

  private fun q98(): Any? {
    return PractitionerAnalysis("PERSONAL_RELATIONSHIPS_COMMUNITY", ap).riskOfSeriousHarm()
  }

  private fun q99(): Any? {
    return PractitionerAnalysis("PERSONAL_RELATIONSHIPS_COMMUNITY", ap).riskOfReoffending()
  }

  private fun qStrength(): Any? {
    return PractitionerAnalysis("PERSONAL_RELATIONSHIPS_COMMUNITY", ap).strengthsOrProtectiveFactors()
  }
}
