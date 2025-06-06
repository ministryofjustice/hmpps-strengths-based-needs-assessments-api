package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer as PersistedAnswer

class OffenceAnalysis : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap = mapOf(
    "o2-1" to ::q1,
    "o2-2_V2_WEAPON" to ::q2Weapon,
    "o2-2_V2_ANYVIOL" to ::q2ViolenceOrCoercion,
    "o2-2_V2_ARSON" to ::q2Arson,
    "o2-2_V2_DOM_ABUSE" to ::q2DomesticAbuse,
    "o2-2_V2_EXCESSIVE" to ::q2ExcessiveOrSadisticViolence,
    "o2-2_V2_PHYSICALDAM" to ::q2PhysicalDamageToProperty,
    "o2-2_V2_SEXUAL" to ::q2SexualElement,
    "o2-3" to ::q3,
    "o2-6" to ::q6,
    "o2-7" to ::q7,
    "o2-7-1" to ::q71,
    "o2-7-2" to ::q72,
    "o2-7-3" to ::q73,
    "o2-8" to ::q8,
    "o2-9_V2_SEXUAL" to ::q9SexualMotivations,
    "o2-9_V2_FINANCIAL" to ::q9FinancialMotivations,
    "o2-9_V2_ADDICTION" to ::q9AddictionMotivations,
    "o2-9_V2_EMOTIONAL" to ::q9EmotionalMotivations,
    "o2-9_V2_RACIAL" to ::q9RacialMotivations,
    "o2-9_V2_THRILL" to ::q29ThrillMotivations,
    "o2-9_V2_OTHER" to ::q29OtherMotivations,
    "o2-9-t_V2" to ::q29t,
    "o2-11" to ::q11,
    "o2-11-t" to ::q11t,
    "o2-12" to ::q12,
    "o2-13" to ::q13,
    "o2-98" to ::q98,
    "o2-99" to ::q99,
  ).plus(buildVictimCollectionAnswers())

  private fun mapVictimAge(entry: Map<String, PersistedAnswer>): String? {
    ap.setContext(Field.OFFENCE_ANALYSIS_VICTIM_AGE)
    return when (entry[Field.OFFENCE_ANALYSIS_VICTIM_AGE.lower]?.value) {
      ap.get(Value.AGE_0_TO_4_YEARS) -> "0"
      ap.get(Value.AGE_5_TO_11_YEARS) -> "1"
      ap.get(Value.AGE_12_TO_15_YEARS) -> "2"
      ap.get(Value.AGE_16_TO_17_YEARS) -> "3"
      ap.get(Value.AGE_18_TO_20_YEARS) -> "4"
      ap.get(Value.AGE_21_TO_25_YEARS) -> "5"
      ap.get(Value.AGE_26_TO_49_YEARS) -> "6"
      ap.get(Value.AGE_50_TO_64_YEARS) -> "7"
      ap.get(Value.AGE_65_AND_OVER) -> "8"
      else -> null
    }
  }

  private fun mapVictimGender(entry: Map<String, PersistedAnswer>): String? {
    ap.setContext(Field.OFFENCE_ANALYSIS_VICTIM_SEX)
    return when (entry[Field.OFFENCE_ANALYSIS_VICTIM_SEX.lower]?.value) {
      ap.get(Value.MALE) -> "1"
      ap.get(Value.FEMALE) -> "2"
      ap.get(Value.UNKNOWN) -> "0"
      else -> null
    }
  }

  private fun mapVictimRace(entry: Map<String, PersistedAnswer>): String? {
    ap.setContext(Field.OFFENCE_ANALYSIS_VICTIM_RACE)
    return when (entry[Field.OFFENCE_ANALYSIS_VICTIM_RACE.lower]?.value) {
      ap.get(Value.WHITE_ENGLISH_WELSH_SCOTTISH_NORTHERN_IRISH_OR_BRITISH) -> "W1"
      ap.get(Value.WHITE_IRISH) -> "W2"
      ap.get(Value.WHITE_GYPSY_OR_IRISH_TRAVELLER) -> "W4"
      ap.get(Value.WHITE_ROMA) -> "W5"
      ap.get(Value.WHITE_ANY_OTHER_WHITE_BACKGROUND) -> "W9"
      ap.get(Value.MIXED_WHITE_AND_BLACK_CARIBBEAN) -> "M1"
      ap.get(Value.MIXED_WHITE_AND_BLACK_AFRICAN) -> "M2"
      ap.get(Value.MIXED_WHITE_AND_ASIAN) -> "M3"
      ap.get(Value.MIXED_ANY_OTHER_MIXED_OR_MULTIPLE_ETHNIC_BACKGROUND_BACKGROUND) -> "M9"
      ap.get(Value.ASIAN_OR_ASIAN_BRITISH_INDIAN) -> "A1"
      ap.get(Value.ASIAN_OR_ASIAN_BRITISH_PAKISTANI) -> "A2"
      ap.get(Value.ASIAN_OR_ASIAN_BRITISH_BANGLADESHI) -> "A3"
      ap.get(Value.ASIAN_OR_ASIAN_BRITISH_CHINESE) -> "A4"
      ap.get(Value.ASIAN_OR_ASIAN_BRITISH_ANY_OTHER_ASIAN_BACKGROUND) -> "A9"
      ap.get(Value.BLACK_OR_BLACK_BRITISH_CARIBBEAN) -> "B1"
      ap.get(Value.BLACK_OR_BLACK_BRITISH_AFRICAN) -> "B2"
      ap.get(Value.BLACK_OR_BLACK_BRITISH_ANY_OTHER_BLACK_BACKGROUND) -> "B9"
      ap.get(Value.ARAB) -> "O2"
      ap.get(Value.ANY_OTHER_ETHNIC_GROUP) -> "O9"
      ap.get(Value.UNKNOWN) -> "NS"
      else -> null
    }
  }

  private fun mapVictimRelationship(entry: Map<String, PersistedAnswer>): String? {
    ap.setContext(Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP)
    return when (entry[Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP.lower]?.value) {
      ap.get(Value.STRANGER) -> "0"
      ap.get(Value.CRIMINAL_JUSTICE_STAFF) -> "12"
      ap.get(Value.POP_PARENT_OR_STEP_PARENT) -> "14"
      ap.get(Value.POP_EX_PARTNER) -> "15"
      ap.get(Value.POP_CHILD_OR_STEP_CHILD) -> "5"
      ap.get(Value.OTHER_FAMILY_MEMBER) -> "6"
      ap.get(Value.POP_PARTNER) -> "1"
      ap.get(Value.OTHER) -> "13"
      else -> null
    }
  }

  private fun buildVictimCollectionAnswers(): FieldsToMap {
    val collection = ap.answer(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION).collection

    return collection.mapIndexed { index, entry ->
      "victim$index" to {
        mapOf(
          "oAGE_OF_VICTIM_ELM" to mapVictimAge(entry),
          "oGENDER_ELM" to mapVictimGender(entry),
          "oETHNIC_CATEGORY_ELM" to mapVictimRace(entry),
          "oVICTIM_RELATION_ELM" to mapVictimRelationship(entry),
        )
      }
    }.toMap()
  }

  private fun q1() = ap.answer(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE).value

  private fun q2Weapon(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.WEAPON))) "YES" else "NO"
  }

  private fun q2ViolenceOrCoercion(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.VIOLENCE_OR_COERCION))) "YES" else "NO"
  }

  private fun q2Arson(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.ARSON))) "YES" else "NO"
  }

  private fun q2DomesticAbuse(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.DOMESTIC_ABUSE))) "YES" else "NO"
  }

  private fun q2ExcessiveOrSadisticViolence(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.EXCESSIVE_OR_SADISTIC_VIOLENCE))) "YES" else "NO"
  }

  private fun q2PhysicalDamageToProperty(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.PHYSICAL_DAMAGE_TO_PROPERTY))) "YES" else "NO"
  }

  private fun q2SexualElement(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
    if (it.contains(ap.get(Value.SEXUAL_ELEMENT))) "YES" else "NO"
  }

  private fun q3(): Any? {
    val answers = ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.mapNotNull {
      when (it) {
        ap.get(Value.VICTIM_TARGETED) -> "DIRECTCONT"
        ap.get(Value.HATRED_OF_IDENTIFIABLE_GROUPS) -> "HATE"
        else -> null
      }
    }.orEmpty().toMutableList()

    ap.answer(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION).collection.find {
      ap.setContext(Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP)
      when (it[Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP.lower]?.value) {
        ap.get(Value.STRANGER) -> true
        else -> false
      }
    }?.run { answers.add("STRANGERS") }

    return if (answers.isNotEmpty()) answers.joinToString(",") else null
  }

  private fun q6(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS).value?.let {
    when (it) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  private fun q7(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED).value?.let {
    when (it) {
      ap.get(Value.ONE),
      ap.get(Value.TWO),
      ap.get(Value.THREE),
      ap.get(Value.FOUR),
      ap.get(Value.FIVE),
      ap.get(Value.SIX_TO_10),
      ap.get(Value.ELEVEN_TO_15),
      ap.get(Value.MORE_THAN_15),
      -> "YES"

      ap.get(Value.NONE) -> "NO"
      else -> null
    }
  }

  private fun q71(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED).value?.let {
    when (it) {
      ap.get(Value.ONE) -> "110"
      ap.get(Value.TWO) -> "120"
      ap.get(Value.THREE) -> "130"
      ap.get(Value.FOUR) -> "140"
      ap.get(Value.FIVE) -> "150"
      ap.get(Value.SIX_TO_10) -> "160"
      ap.get(Value.ELEVEN_TO_15) -> "170"
      ap.get(Value.MORE_THAN_15) -> "180"
      else -> null
    }
  }

  private fun q72(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.PRESSURISED_BY_OTHERS))) "YES" else "NO"
  }

  private fun q73(): Any? = when (ap.answer(Field.OFFENCE_ANALYSIS_LEADER).value) {
    ap.get(Value.YES) -> {
      val details = ap.answer(Field.OFFENCE_ANALYSIS_LEADER_YES_DETAILS).value
      listOfNotNull("Yes", details).joinToString(" - ")
    }

    ap.get(Value.NO) -> {
      val details = ap.answer(Field.OFFENCE_ANALYSIS_LEADER_NO_DETAILS).value
      listOfNotNull("No", details).joinToString(" - ")
    }

    else -> null
  }

  private fun q8(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_REASON).value

  private fun q9SexualMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.SEXUAL_MOTIVATION))) "YES" else "NO"
  }

  private fun q9FinancialMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.FINANCIAL_MOTIVATION))) "YES" else "NO"
  }

  private fun q9AddictionMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.ADDICTIONS_OR_PERCEIVED_NEEDS))) "YES" else "NO"
  }

  private fun q9EmotionalMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.EMOTIONAL_STATE))) "YES" else "NO"
  }

  private fun q9RacialMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.HATRED_OF_IDENTIFIABLE_GROUPS))) "YES" else "NO"
  }

  private fun q29ThrillMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.THRILL_SEEKING))) "YES" else "NO"
  }

  private fun q29OtherMotivations(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
    if (it.contains(ap.get(Value.OTHER))) "YES" else "NO"
  }

  private fun q29t(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS_OTHER_DETAILS).value

  private fun q11(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY).value?.let {
    when (it) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  private fun q11t(): Any? = when (ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY).value) {
    ap.get(Value.YES) -> ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_YES_DETAILS).value
    ap.get(Value.NO) -> ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_NO_DETAILS).value
    else -> null
  }

  private fun q12(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING).value

  private fun q13(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_ESCALATION).value?.let {
    when (it) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }

  private fun q98(): Any? = when (ap.answer(Field.OFFENCE_ANALYSIS_RISK).value) {
    ap.get(Value.YES) -> ap.answer(Field.OFFENCE_ANALYSIS_RISK_YES_DETAILS).value
    ap.get(Value.NO) -> ap.answer(Field.OFFENCE_ANALYSIS_RISK_NO_DETAILS).value
    else -> null
  }

  private fun q99(): Any? = ap.answer(Field.OFFENCE_ANALYSIS_RISK).value?.let {
    when (it) {
      ap.get(Value.YES) -> "YES"
      ap.get(Value.NO) -> "NO"
      else -> null
    }
  }
}
