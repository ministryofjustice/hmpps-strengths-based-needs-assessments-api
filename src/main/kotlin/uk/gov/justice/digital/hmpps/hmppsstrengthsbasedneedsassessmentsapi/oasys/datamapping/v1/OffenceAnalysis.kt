package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class OffenceAnalysis : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap {
    return mapOf(
      "o2-1" to ::q1,
      "o2-2_V2_WEAPON" to ::q2Weapon,
      "o2-2_V2_ANYVIOL" to ::q2ViolenceOrCoercion,
      "o2-2_V2_ARSON" to ::q2Arson,
      "o2-2_V2_DOM_ABUSE" to ::q2DomesticAbuse,
      "o2-2_V2_EXCESSIVE" to ::q2ExcessiveOrSadisticViolence,
      "o2-2_V2_PHYSICALDAM" to ::q2PhysicalDamageToProperty,
      "o2-2_V2_SEXUAL" to ::q2SexualElement,
      "o2-DIRECTCONT" to ::q2DirectContact,
      "o2-HATE" to ::q2HatredOfIdentifiableGroups,
      // TODO: Figure this out..
      // "o-STRANGERS" to ::qStrangers,
      // "o2-victim.age_of_victim_ELM" to ::qTodo,
      // "o2-victim.gender_ELM" to ::qTodo,
      // "o2-victim_ethnic_category_ELM" to ::qTodo,
      // "o2-2_4_2" to ::q242,
      "o2-6" to ::q6,
      "o2-7" to ::q7,
      "o2-7-1" to ::q71,
      "o2-7-2" to ::q72,
      "o2-7-3" to ::q73,
      "o2-8" to ::q8,
      "o2-9_V2_SEXUAL" to ::q9SexualMotivations,
      "o2-9_V2_FINANCIAL" to ::q9FinancialMotivations,
      "o2-9_V2_ADDICTION" to ::q9AddictionMotivations,
      "o2.9_V2_RACIAL" to ::q9RacialMotivations,
      "o2-2_9_V2_THRILL" to ::q29ThrillMotivations,
      "o2-2_9_V2_OTHER" to ::q29OtherMotivations,
      "o2-2_9t_V2" to ::q29t,
      "o2-11" to ::q11,
      "o2-11t" to ::q11t,
      "o2-12" to ::q12,
      "o2-13" to ::q13,
      "o2-98" to ::q98,
      "o2-99" to ::q99,
    )
  }

  private fun q1() = ap.answer(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE).value

  private fun q2Weapon(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.WEAPON))) "YES" else "NO"
    }
  }

  private fun q2ViolenceOrCoercion(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.VIOLENCE_OR_COERCION))) "YES" else "NO"
    }
  }

  private fun q2Arson(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.ARSON))) "YES" else "NO"
    }
  }

  private fun q2DomesticAbuse(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.DOMESTIC_ABUSE))) "YES" else "NO"
    }
  }

  private fun q2ExcessiveOrSadisticViolence(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.EXCESSIVE_OR_SADISTIC_VIOLENCE))) "YES" else "NO"
    }
  }

  private fun q2PhysicalDamageToProperty(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.PHYSICAL_DAMAGE_TO_PROPERTY))) "YES" else "NO"
    }
  }

  private fun q2SexualElement(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.SEXUAL_ELEMENT))) "YES" else "NO"
    }
  }

  private fun q2DirectContact(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.VICTIM_TARGETED))) "YES" else "NO"
    }
  }

  private fun q2HatredOfIdentifiableGroups(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ELEMENTS).values?.let {
      if (it.contains(ap.get(Value.HATRED_OF_IDENTIFIABLE_GROUPS))) "YES" else "NO"
    }
  }

  private fun qStrangers(): Any? {
    return null
  }

  // ...

  private fun q242(): Any? {
    return null
  }

  private fun q6(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS).value?.let {
      when (it) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> null
      }
    }
  }

  private fun q7(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED).value?.let {
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
  }

  private fun q71(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED).value?.let {
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
  }

  private fun q72(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.PRESSURISED_BY_OTHERS))) "YES" else "NO"
    }
  }

  private fun q73(): Any? {
    return when (ap.answer(Field.OFFENCE_ANALYSIS_LEADER).value) {
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
  }

  private fun q8(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_REASON).value
  }

  private fun q9SexualMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.SEXUAL_MOTIVATION))) "YES" else "NO"
    }
  }

  private fun q9FinancialMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.FINANCIAL_MOTIVATION))) "YES" else "NO"
    }
  }

  private fun q9AddictionMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.ADDICTIONS_OR_PERCEIVED_NEEDS))) "YES" else "NO"
    }
  }

  private fun q9RacialMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.HATRED_OF_IDENTIFIABLE_GROUPS))) "YES" else "NO"
    }
  }

  private fun q29ThrillMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.THRILL_SEEKING))) "YES" else "NO"
    }
  }

  private fun q29OtherMotivations(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS).values?.let {
      if (it.contains(ap.get(Value.OTHER))) "YES" else "NO"
    }
  }

  private fun q29t(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_MOTIVATIONS_OTHER_DETAILS).value
  }

  private fun q11(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY).value?.let {
      when (it) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> null
      }
    }
  }

  private fun q11t(): Any? {
    return when (ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY).value) {
      ap.get(Value.YES) -> ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_YES_DETAILS).value
      ap.get(Value.NO) -> ap.answer(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_NO_DETAILS).value
      else -> null
    }
  }

  private fun q12(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING).value
  }

  private fun q13(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_ESCALATION).value?.let {
      when (it) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> null
      }
    }
  }

  private fun q98(): Any? {
    return when (ap.answer(Field.OFFENCE_ANALYSIS_RISK).value) {
      ap.get(Value.YES) -> ap.answer(Field.OFFENCE_ANALYSIS_RISK_YES_DETAILS).value
      ap.get(Value.NO) -> ap.answer(Field.OFFENCE_ANALYSIS_RISK_NO_DETAILS).value
      else -> null
    }
  }

  private fun q99(): Any? {
    return ap.answer(Field.OFFENCE_ANALYSIS_RISK).value?.let {
      when (it) {
        ap.get(Value.YES) -> "YES"
        ap.get(Value.NO) -> "NO"
        else -> null
      }
    }
  }
}
