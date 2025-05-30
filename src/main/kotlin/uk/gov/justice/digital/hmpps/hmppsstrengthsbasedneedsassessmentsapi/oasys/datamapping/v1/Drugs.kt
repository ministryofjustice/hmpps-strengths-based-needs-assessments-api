package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.FieldsToMap
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping

class Drugs : SectionMapping() {
  override fun getFieldsToMap(): FieldsToMap = mapOf(
    "o8-1" to ::q1,
    "o8-2-1-1" to ::q2011,
    "o8-2-1-3" to ::q2013,
    "o8-2-1-2" to ::q2012,
    "o8-2-1-4" to ::q2014,
    "o8-2-2-1" to ::q2021,
    "o8-2-2-3" to ::q2023,
    "o8-2-2-2" to ::q2022,
    "o8-2-2-4" to ::q2024,
    "o8-2-3-1" to ::q2031,
    "o8-2-3-3" to ::q2033,
    "o8-2-3-2" to ::q2032,
    "o8-2-3-4" to ::q2034,
    "o8-2-4-1" to ::q2041,
    "o8-2-4-2" to ::q2042,
    "o8-2-4-3" to ::q2043,
    "o8-2-4-4" to ::q2044,
    "o8-2-5-1" to ::q2051,
    "o8-2-5-2" to ::q2052,
    "o8-2-5-3" to ::q2053,
    "o8-2-5-4" to ::q2054,
    "o8-2-6-1" to ::q2061,
    "o8-2-6-2" to ::q2062,
    "o8-2-6-3" to ::q2063,
    "o8-2-6-4" to ::q2064,
    "o8-2-7-1" to ::q2071,
    "o8-2-7-3" to ::q2073,
    "o8-2-7-2" to ::q2072,
    "o8-2-7-4" to ::q2074,
    "o8-2-8-1" to ::q2081,
    "o8-2-8-2" to ::q2082,
    "o8-2-8-3" to ::q2083,
    "o8-2-8-4" to ::q2084,
    "o8-2-9-1" to ::q2091,
    "o8-2-9-3" to ::q2093,
    "o8-2-10-1" to ::q2101,
    "o8-2-10-3" to ::q2103,
    "o8-2-11-1" to ::q2111,
    "o8-2-11-3" to ::q2113,
    "o8-2-12-1" to ::q2121,
    "o8-2-12-3" to ::q2123,
    "o8-2-13-1" to ::q2131,
    "o8-2-13-2" to ::q2132,
    "o8-2-13-3" to ::q2133,
    "o8-2-13-4" to ::q2134,
    "o8-2-15-1" to ::q2151,
    "o8-2-15-3" to ::q2153,
    "o8-2-14-1" to ::q2141,
    "o8-2-14-2" to ::q2142,
    "o8-2-14-3" to ::q2143,
    "o8-2-14-4" to ::q2144,
    "o8-2-14-t" to ::q214t,
    "o8-4" to ::q4,
    "o8-5" to ::q5,
    "o8-6" to ::q6,
    "o8-8" to ::q8,
    "o8-97" to ::q97,
    "o8-98" to ::q98,
    "o8-99" to ::q99,
    "o8_SAN_STRENGTH" to ::qStrength,
  )

  private fun getUsageFrequencyScore(field: Field): Any? = when (ap.answer(field).value) {
    ap.get(Value.DAILY) -> "100"
    ap.get(Value.WEEKLY) -> "110"
    ap.get(Value.MONTHLY) -> "120"
    ap.get(Value.OCCASIONALLY) -> "130"
    else -> null
  }

  private fun isUsing(field: Field, frequencies: Set<Value>): Boolean {
    val usage = ap.answer(field).value ?: return false
    return when {
      frequencies.map { ap.get(it) }.contains(usage) -> true
      else -> false
    }
  }

  private fun isMoreThanSix(field: Field): Boolean = when (ap.answer(field).value) {
    ap.get(Value.MORE_THAN_SIX) -> true
    else -> false
  }

  private fun isLastSix(field: Field): Boolean = when (ap.answer(field).value) {
    ap.get(Value.LAST_SIX) -> true
    else -> false
  }

  private fun fieldContains(field: Field, value: Value): String? = ap.answer(field).values?.let {
    if (it.contains(ap.get(value))) "YES" else null
  }

  private fun q1(): Any? = when (ap.answer(Field.DRUG_USE).value) {
    ap.get(Value.YES) -> "YES"
    ap.get(Value.NO) -> "NO"
    else -> null
  }

  private fun q2011(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN)

  private fun q2013(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_HEROIN)) "YES" else null

  private fun q2012(): Any? = fieldContains(Field.DRUGS_INJECTED_HEROIN, Value.LAST_SIX)

  private fun q2014(): Any? = fieldContains(Field.DRUGS_INJECTED_HEROIN, Value.MORE_THAN_SIX)

  private fun q2021(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED)

  private fun q2023(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED)) "YES" else null

  private fun q2022(): Any? = fieldContains(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, Value.LAST_SIX)

  private fun q2024(): Any? = fieldContains(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, Value.MORE_THAN_SIX)

  private fun q2031(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES)

  private fun q2033(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_OTHER_OPIATES)) "YES" else null

  private fun q2032(): Any? = fieldContains(Field.DRUGS_INJECTED_OTHER_OPIATES, Value.LAST_SIX)

  private fun q2034(): Any? = fieldContains(Field.DRUGS_INJECTED_OTHER_OPIATES, Value.MORE_THAN_SIX)

  private fun q2041(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK)

  private fun q2043(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_CRACK)) "YES" else null

  private fun q2042(): Any? = fieldContains(Field.DRUGS_INJECTED_CRACK, Value.LAST_SIX)

  private fun q2044(): Any? = fieldContains(Field.DRUGS_INJECTED_CRACK, Value.MORE_THAN_SIX)

  private fun q2051(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE)

  private fun q2053(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_COCAINE)) "YES" else null

  private fun q2052(): Any? = fieldContains(Field.DRUGS_INJECTED_COCAINE, Value.LAST_SIX)

  private fun q2054(): Any? = fieldContains(Field.DRUGS_INJECTED_COCAINE, Value.MORE_THAN_SIX)

  private fun q2061(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS)

  private fun q2063(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS)) "YES" else null

  private fun q2062(): Any? = fieldContains(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, Value.LAST_SIX)

  private fun q2064(): Any? = fieldContains(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, Value.MORE_THAN_SIX)

  private fun q2071(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES)

  private fun q2073(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_BENZODIAZEPINES)) "YES" else null

  private fun q2072(): Any? = fieldContains(Field.DRUGS_INJECTED_BENZODIAZEPINES, Value.LAST_SIX)

  private fun q2074(): Any? = fieldContains(Field.DRUGS_INJECTED_BENZODIAZEPINES, Value.MORE_THAN_SIX)

  private fun q2081(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES)

  private fun q2083(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_AMPHETAMINES)) "YES" else null

  private fun q2082(): Any? = fieldContains(Field.DRUGS_INJECTED_AMPHETAMINES, Value.LAST_SIX)

  private fun q2084(): Any? = fieldContains(Field.DRUGS_INJECTED_AMPHETAMINES, Value.MORE_THAN_SIX)

  private fun q2091(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS)

  private fun q2093(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_HALLUCINOGENICS)) "YES" else null

  private fun q2101(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY)

  private fun q2103(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_ECSTASY)) "YES" else null

  private fun q2111(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS)

  private fun q2113(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_CANNABIS)) "YES" else null

  private fun q2121(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS)

  private fun q2123(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_SOLVENTS)) "YES" else null

  private fun q2131(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS)

  private fun q2133(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_STEROIDS)) "YES" else null

  private fun q2132(): Any? = fieldContains(Field.DRUGS_INJECTED_STEROIDS, Value.LAST_SIX)

  private fun q2134(): Any? = fieldContains(Field.DRUGS_INJECTED_STEROIDS, Value.MORE_THAN_SIX)

  private fun q2151(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE)

  private fun q2153(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_SPICE)) "YES" else null

  private fun q2141(): Any? = getUsageFrequencyScore(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG)

  private fun q2143(): Any? = if (isMoreThanSix(Field.DRUG_LAST_USED_OTHER_DRUG)) "YES" else null

  private fun q2142(): Any? = fieldContains(Field.DRUGS_INJECTED_OTHER_DRUG, Value.LAST_SIX)

  private fun q2144(): Any? = fieldContains(Field.DRUGS_INJECTED_OTHER_DRUG, Value.MORE_THAN_SIX)

  private fun q214t(): Any? = ap.answer(Field.OTHER_DRUG_NAME).value

  private fun q4(): Any {
    val frequencies = setOf(Value.DAILY, Value.WEEKLY, Value.MONTHLY, Value.OCCASIONALLY)
    return when {
      isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, frequencies) ||
        isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, frequencies) ||
        isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, frequencies) ||
        isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, frequencies) ||
        isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, frequencies) ||
        isUsing(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, frequencies)
      -> "2"
      else -> "0"
    }
  }

  private fun q5(): Any {
    val dailyOrWeekly = setOf(Value.DAILY, Value.WEEKLY)
    val monthlyOrOccasionally = setOf(Value.MONTHLY, Value.OCCASIONALLY)
    val drugs = setOf(
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE,
      Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG,
    )

    return when {
      drugs.any { isUsing(it, dailyOrWeekly) } -> "2"
      drugs.any { isUsing(it, monthlyOrOccasionally) } -> "0"
      isLastSix(Field.DRUG_LAST_USED_AMPHETAMINES) ||
        isLastSix(Field.DRUG_LAST_USED_BENZODIAZEPINES) ||
        isLastSix(Field.DRUG_LAST_USED_CANNABIS) ||
        isLastSix(Field.DRUG_LAST_USED_COCAINE) ||
        isLastSix(Field.DRUG_LAST_USED_CRACK) ||
        isLastSix(Field.DRUG_LAST_USED_ECSTASY) ||
        isLastSix(Field.DRUG_LAST_USED_HALLUCINOGENICS) ||
        isLastSix(Field.DRUG_LAST_USED_HEROIN) ||
        isLastSix(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED) ||
        isLastSix(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS) ||
        isLastSix(Field.DRUG_LAST_USED_OTHER_OPIATES) ||
        isLastSix(Field.DRUG_LAST_USED_SOLVENTS) ||
        isLastSix(Field.DRUG_LAST_USED_STEROIDS) ||
        isLastSix(Field.DRUG_LAST_USED_SPICE) ||
        isLastSix(Field.DRUG_LAST_USED_OTHER_DRUG) -> "M"
      else -> "" // TODO: Check what this should be.
    }
  }

  private fun q6(): Any = when {
    isLastSix(Field.DRUGS_INJECTED_HEROIN) ||
      isLastSix(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED) ||
      isLastSix(Field.DRUGS_INJECTED_OTHER_OPIATES) ||
      isLastSix(Field.DRUGS_INJECTED_CRACK) ||
      isLastSix(Field.DRUGS_INJECTED_COCAINE) ||
      isLastSix(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS) ||
      isLastSix(Field.DRUGS_INJECTED_BENZODIAZEPINES) ||
      isLastSix(Field.DRUGS_INJECTED_AMPHETAMINES) ||
      isLastSix(Field.DRUGS_INJECTED_STEROIDS) ||
      isLastSix(Field.DRUGS_INJECTED_OTHER_DRUG)
    -> "2"

    isMoreThanSix(Field.DRUGS_INJECTED_HEROIN) ||
      isMoreThanSix(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED) ||
      isMoreThanSix(Field.DRUGS_INJECTED_OTHER_OPIATES) ||
      isMoreThanSix(Field.DRUGS_INJECTED_CRACK) ||
      isMoreThanSix(Field.DRUGS_INJECTED_COCAINE) ||
      isMoreThanSix(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS) ||
      isMoreThanSix(Field.DRUGS_INJECTED_BENZODIAZEPINES) ||
      isMoreThanSix(Field.DRUGS_INJECTED_AMPHETAMINES) ||
      isMoreThanSix(Field.DRUGS_INJECTED_STEROIDS) ||
      isMoreThanSix(Field.DRUGS_INJECTED_OTHER_DRUG)
    -> "1"

    else -> "0"
  }

  private fun q8(): Any? = when (ap.answer(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP).value) {
    ap.get(Value.FULL_MOTIVATION) -> "0"
    ap.get(Value.PARTIAL_MOTIVATION) -> "1"
    ap.get(Value.NO_MOTIVATION) -> "2"
    ap.get(Value.UNKNOWN) -> "M"
    else -> null
  }

  private fun q97(): Any? = PractitionerAnalysis("DRUG_USE", ap).notes()

  private fun q98(): Any? = PractitionerAnalysis("DRUG_USE", ap).riskOfSeriousHarm()

  private fun q99(): Any? = PractitionerAnalysis("DRUG_USE", ap).riskOfReoffending()

  private fun qStrength(): Any? = PractitionerAnalysis("DRUG_USE", ap).strengthsOrProtectiveFactors()
}
