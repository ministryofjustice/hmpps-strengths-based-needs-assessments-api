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

  private fun isYes(field: Field): Boolean = when (ap.answer(field).value) {
    ap.get(Value.YES) -> true
    else -> false
  }

  private fun q1(): Any? = when (ap.answer(Field.DRUG_USE).value) {
    ap.get(Value.YES) -> "YES"
    ap.get(Value.NO) -> "NO"
    else -> null
  }

  private fun q2011(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_HEROIN)

  private fun q2013(): Any? = if (isYes(Field.PAST_DRUG_USAGE_HEROIN)) "YES" else null

  private fun q2012(): Any? = if (isYes(Field.INJECTING_DRUG_HEROIN)) "YES" else null

  private fun q2014(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_HEROIN)) "YES" else null

  private fun q2021(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED)

  private fun q2023(): Any? = if (isYes(Field.PAST_DRUG_USAGE_METHADONE_NOT_PRESCRIBED)) "YES" else null

  private fun q2022(): Any? = if (isYes(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED)) "YES" else null

  private fun q2024(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED)) "YES" else null

  private fun q2031(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_OTHER_OPIATES)

  private fun q2033(): Any? = if (isYes(Field.PAST_DRUG_USAGE_OTHER_OPIATES)) "YES" else null

  private fun q2032(): Any? = if (isYes(Field.INJECTING_DRUG_OTHER_OPIATES)) "YES" else null

  private fun q2034(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_OTHER_OPIATES)) "YES" else null

  private fun q2041(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_CRACK)

  private fun q2043(): Any? = if (isYes(Field.PAST_DRUG_USAGE_CRACK)) "YES" else null

  private fun q2042(): Any? = if (isYes(Field.INJECTING_DRUG_CRACK)) "YES" else null

  private fun q2044(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_CRACK)) "YES" else null

  private fun q2051(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_COCAINE)

  private fun q2053(): Any? = if (isYes(Field.PAST_DRUG_USAGE_COCAINE)) "YES" else null

  private fun q2052(): Any? = if (isYes(Field.INJECTING_DRUG_COCAINE)) "YES" else null

  private fun q2054(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_COCAINE)) "YES" else null

  private fun q2061(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS)

  private fun q2063(): Any? = if (isYes(Field.PAST_DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS)) "YES" else null

  private fun q2062(): Any? = if (isYes(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS)) "YES" else null

  private fun q2064(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS)) "YES" else null

  private fun q2071(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_BENZODIAZEPINES)

  private fun q2073(): Any? = if (isYes(Field.PAST_DRUG_USAGE_BENZODIAZEPINES)) "YES" else null

  private fun q2072(): Any? = if (isYes(Field.INJECTING_DRUG_BENZODIAZEPINES)) "YES" else null

  private fun q2074(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES)) "YES" else null

  private fun q2081(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_AMPHETAMINES)

  private fun q2083(): Any? = if (isYes(Field.PAST_DRUG_USAGE_AMPHETAMINES)) "YES" else null

  private fun q2082(): Any? = if (isYes(Field.INJECTING_DRUG_AMPHETAMINES)) "YES" else null

  private fun q2084(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_AMPHETAMINES)) "YES" else null

  private fun q2091(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_HALLUCINOGENICS)

  private fun q2093(): Any? = if (isYes(Field.PAST_DRUG_USAGE_HALLUCINOGENICS)) "YES" else null

  private fun q2101(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_ECSTASY)

  private fun q2103(): Any? = if (isYes(Field.PAST_DRUG_USAGE_ECSTASY)) "YES" else null

  private fun q2111(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_CANNABIS)

  private fun q2113(): Any? = if (isYes(Field.PAST_DRUG_USAGE_CANNABIS)) "YES" else null

  private fun q2121(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_SOLVENTS)

  private fun q2123(): Any? = if (isYes(Field.PAST_DRUG_USAGE_SOLVENTS)) "YES" else null

  private fun q2131(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_STEROIDS)

  private fun q2133(): Any? = if (isYes(Field.PAST_DRUG_USAGE_STEROIDS)) "YES" else null

  private fun q2132(): Any? = if (isYes(Field.INJECTING_DRUG_STEROIDS)) "YES" else null

  private fun q2134(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_STEROIDS)) "YES" else null

  private fun q2151(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_SPICE)

  private fun q2153(): Any? = if (isYes(Field.PAST_DRUG_USAGE_SPICE)) "YES" else null

  private fun q2141(): Any? = getUsageFrequencyScore(Field.DRUG_USAGE_OTHER_DRUG)

  private fun q2143(): Any? = if (isYes(Field.PAST_DRUG_USAGE_OTHER_DRUG)) "YES" else null

  private fun q2142(): Any? = if (isYes(Field.INJECTING_DRUG_OTHER_DRUG)) "YES" else null

  private fun q2144(): Any? = if (isYes(Field.PAST_INJECTING_DRUG_OTHER_DRUG)) "YES" else null

  private fun q214t(): Any? {
    return ap.answer(Field.DRUG_USE_TYPE_OTHER_DRUG_DETAILS).value
    return null
  }

  private fun q4(): Any {
    val frequencies = setOf(Value.DAILY, Value.WEEKLY, Value.MONTHLY, Value.OCCASIONALLY)
    return when {
      isUsing(Field.DRUG_USAGE_HEROIN, frequencies) ||
        isUsing(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, frequencies) ||
        isUsing(Field.DRUG_USAGE_OTHER_OPIATES, frequencies) ||
        isUsing(Field.DRUG_USAGE_CRACK, frequencies) ||
        isUsing(Field.DRUG_USAGE_COCAINE, frequencies) ||
        isUsing(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, frequencies)
      -> "2"
      else -> "0"
    }
  }

  private fun q5(): Any {
    val dailyOrWeekly = setOf(Value.DAILY, Value.WEEKLY)
    val monthlyOrOccasionally = setOf(Value.MONTHLY, Value.OCCASIONALLY)
    val drugs = setOf(
      Field.DRUG_USAGE_HEROIN,
      Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED,
      Field.DRUG_USAGE_OTHER_OPIATES,
      Field.DRUG_USAGE_CRACK,
      Field.DRUG_USAGE_COCAINE,
      Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS,
      Field.DRUG_USAGE_BENZODIAZEPINES,
      Field.DRUG_USAGE_AMPHETAMINES,
      Field.DRUG_USAGE_HALLUCINOGENICS,
      Field.DRUG_USAGE_ECSTASY,
      Field.DRUG_USAGE_CANNABIS,
      Field.DRUG_USAGE_SOLVENTS,
      Field.DRUG_USAGE_STEROIDS,
      Field.DRUG_USAGE_SPICE,
      Field.DRUG_USAGE_OTHER_DRUG,
    )

    return when {
      drugs.any { isUsing(it, dailyOrWeekly) } -> "2"
      drugs.any { isUsing(it, monthlyOrOccasionally) } -> "0"
      else -> "M"
    }
  }

  private fun q6(): Any = when {
    isYes(Field.INJECTING_DRUG_HEROIN) ||
      isYes(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED) ||
      isYes(Field.INJECTING_DRUG_OTHER_OPIATES) ||
      isYes(Field.INJECTING_DRUG_CRACK) ||
      isYes(Field.INJECTING_DRUG_COCAINE) ||
      isYes(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS) ||
      isYes(Field.INJECTING_DRUG_BENZODIAZEPINES) ||
      isYes(Field.INJECTING_DRUG_AMPHETAMINES) ||
      isYes(Field.INJECTING_DRUG_STEROIDS) ||
      isYes(Field.INJECTING_DRUG_OTHER_DRUG)
    -> "2"

    isYes(Field.PAST_INJECTING_DRUG_HEROIN) ||
      isYes(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED) ||
      isYes(Field.PAST_INJECTING_DRUG_OTHER_OPIATES) ||
      isYes(Field.PAST_INJECTING_DRUG_CRACK) ||
      isYes(Field.PAST_INJECTING_DRUG_COCAINE) ||
      isYes(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS) ||
      isYes(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES) ||
      isYes(Field.PAST_INJECTING_DRUG_AMPHETAMINES) ||
      isYes(Field.PAST_INJECTING_DRUG_STEROIDS) ||
      isYes(Field.PAST_INJECTING_DRUG_OTHER_DRUG)
    -> "1"

    else -> "0"
  }

  private fun q8(): Any? = when (ap.answer(Field.MOTIVATED_STOPPING_DRUG_USE).value) {
    ap.get(Value.MOTIVATED) -> "0"
    ap.get(Value.SOME_MOTIVATION) -> "1"
    ap.get(Value.NO_MOTIVATION) -> "2"
    else -> null
  }

  private fun q97(): Any? = PractitionerAnalysis("DRUG_USE", ap).notes()

  private fun q98(): Any? = PractitionerAnalysis("DRUG_USE", ap).riskOfSeriousHarm()

  private fun q99(): Any? = PractitionerAnalysis("DRUG_USE", ap).riskOfReoffending()

  private fun qStrength(): Any? = PractitionerAnalysis("DRUG_USE", ap).strengthsOrProtectiveFactors()
}
