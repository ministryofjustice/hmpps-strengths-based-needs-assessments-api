package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import kotlin.test.Test

class DrugsTest : SectionMappingTest(Drugs(), "1.0") {
  @Test
  fun q1() {
    test(
      "o8-1",
      Given().expect(null),
      Given(Field.DRUG_USE, null).expect(null),
      Given(Field.DRUG_USE, Value.YES).expect("YES"),
      Given(Field.DRUG_USE, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q2011() {
    test(
      "o8-2-1-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2013() {
    test(
      "o8-2-1-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_HEROIN, null).expect(null),
      Given(Field.DRUG_LAST_USED_HEROIN, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_HEROIN, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2012() {
    test(
      "o8-2-1-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, null).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2014() {
    test(
      "o8-2-1-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, null).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2021() {
    test(
      "o8-2-2-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2023() {
    test(
      "o8-2-2-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2022() {
    test(
      "o8-2-2-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2024() {
    test(
      "o8-2-2-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2031() {
    test(
      "o8-2-3-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2033() {
    test(
      "o8-2-3-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_OPIATES, null).expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_OPIATES, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_OPIATES, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2032() {
    test(
      "o8-2-3-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, null).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2034() {
    test(
      "o8-2-3-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, null).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2041() {
    test(
      "o8-2-4-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2043() {
    test(
      "o8-2-4-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_CRACK, null).expect(null),
      Given(Field.DRUG_LAST_USED_CRACK, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_CRACK, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2042() {
    test(
      "o8-2-4-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, null).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2044() {
    test(
      "o8-2-4-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, null).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_CRACK, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2051() {
    test(
      "o8-2-5-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2053() {
    test(
      "o8-2-5-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_COCAINE, null).expect(null),
      Given(Field.DRUG_LAST_USED_COCAINE, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_COCAINE, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2052() {
    test(
      "o8-2-5-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, null).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2054() {
    test(
      "o8-2-5-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, null).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_COCAINE, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2061() {
    test(
      "o8-2-6-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2063() {
    test(
      "o8-2-6-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2062() {
    test(
      "o8-2-6-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2064() {
    test(
      "o8-2-6-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2071() {
    test(
      "o8-2-7-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2073() {
    test(
      "o8-2-7-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_BENZODIAZEPINES, null).expect(null),
      Given(Field.DRUG_LAST_USED_BENZODIAZEPINES, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_BENZODIAZEPINES, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2072() {
    test(
      "o8-2-7-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, null).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2074() {
    test(
      "o8-2-7-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, null).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2081() {
    test(
      "o8-2-8-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2083() {
    test(
      "o8-2-8-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_AMPHETAMINES, null).expect(null),
      Given(Field.DRUG_LAST_USED_AMPHETAMINES, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_AMPHETAMINES, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2082() {
    test(
      "o8-2-8-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, null).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2084() {
    test(
      "o8-2-8-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, null).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2091() {
    test(
      "o8-2-9-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2093() {
    test(
      "o8-2-9-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_HALLUCINOGENICS, null).expect(null),
      Given(Field.DRUG_LAST_USED_HALLUCINOGENICS, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_HALLUCINOGENICS, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2101() {
    test(
      "o8-2-10-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2103() {
    test(
      "o8-2-10-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_ECSTASY, null).expect(null),
      Given(Field.DRUG_LAST_USED_ECSTASY, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_ECSTASY, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2111() {
    test(
      "o8-2-11-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2113() {
    test(
      "o8-2-11-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_CANNABIS, null).expect(null),
      Given(Field.DRUG_LAST_USED_CANNABIS, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_CANNABIS, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2121() {
    test(
      "o8-2-12-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2123() {
    test(
      "o8-2-12-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_SOLVENTS, null).expect(null),
      Given(Field.DRUG_LAST_USED_SOLVENTS, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_SOLVENTS, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2131() {
    test(
      "o8-2-13-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2133() {
    test(
      "o8-2-13-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_STEROIDS, null).expect(null),
      Given(Field.DRUG_LAST_USED_STEROIDS, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_STEROIDS, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2132() {
    test(
      "o8-2-13-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, null).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2134() {
    test(
      "o8-2-13-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, null).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_STEROIDS, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2151() {
    test(
      "o8-2-15-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2153() {
    test(
      "o8-2-15-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_SPICE, null).expect(null),
      Given(Field.DRUG_LAST_USED_SPICE, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_SPICE, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2141() {
    test(
      "o8-2-14-1",
      Given().expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, null).expect(null),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.DAILY).expect("100"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.WEEKLY).expect("110"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.MONTHLY).expect("120"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2143() {
    test(
      "o8-2-14-3",
      Given().expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_DRUG, null).expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_DRUG, Value.LAST_SIX).expect(null),
      Given(Field.DRUG_LAST_USED_OTHER_DRUG, Value.MORE_THAN_SIX).expect("YES"),
    )
  }

  @Test
  fun q2142() {
    test(
      "o8-2-14-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, null).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, listOf(Value.MORE_THAN_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, listOf(Value.LAST_SIX)).expect("YES"),
    )
  }

  @Test
  fun q2144() {
    test(
      "o8-2-14-4",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, null).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, emptyList()).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, listOf(Value.LAST_SIX)).expect(null),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, listOf(Value.MORE_THAN_SIX)).expect("YES"),
    )
  }

  @Test
  fun q214t() {
    test(
      "o8-2-14-t",
      Given().expect(null),
      Given(Field.OTHER_DRUG_NAME, null).expect(null),
      Given(Field.OTHER_DRUG_NAME, "").expect(""),
      Given(Field.OTHER_DRUG_NAME, "some text").expect("some text"),
    )
  }

  @Test
  fun q4() {
    test(
      "o8-4",
      Given().expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, null).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.OCCASIONALLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.OCCASIONALLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.OCCASIONALLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.OCCASIONALLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o8-5",
      Given().expect(""), // TODO: Check these, as "M" seems to have been repurposed.
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, null).expect(""),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, null).expect(""),

      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.DAILY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.DAILY).expect("2"),

      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.WEEKLY).expect("2"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.WEEKLY).expect("2"),

      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.MONTHLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.MONTHLY).expect("0"),

      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_OPIATES, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CRACK, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_BENZODIAZEPINES, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_AMPHETAMINES, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HALLUCINOGENICS, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_ECSTASY, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_CANNABIS, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SOLVENTS, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_STEROIDS, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_SPICE, Value.OCCASIONALLY).expect("0"),
      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_OTHER_DRUG, Value.OCCASIONALLY).expect("0"),

      Given(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_HEROIN, Value.DAILY)
        .and(Field.HOW_OFTEN_USED_LAST_SIX_MONTHS_COCAINE, Value.MONTHLY)
        .expect("2"),

      Given(Field.DRUG_LAST_USED_AMPHETAMINES, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_BENZODIAZEPINES, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_CANNABIS, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_COCAINE, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_CRACK, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_ECSTASY, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_HALLUCINOGENICS, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_HEROIN, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_METHADONE_NOT_PRESCRIBED, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_MISUSED_PRESCRIBED_DRUGS, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_OTHER_OPIATES, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_SOLVENTS, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_STEROIDS, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_SPICE, Value.LAST_SIX).expect("M"),
      Given(Field.DRUG_LAST_USED_OTHER_DRUG, Value.LAST_SIX).expect("M"),
    )
  }

  @Test
  fun q6() {
    test(
      "o8-6",
      Given().expect("0"),

      Given(Field.DRUGS_INJECTED_HEROIN, null).expect("0"),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, null).expect("0"),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, null).expect("0"),
      Given(Field.DRUGS_INJECTED_CRACK, null).expect("0"),
      Given(Field.DRUGS_INJECTED_COCAINE, null).expect("0"),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, null).expect("0"),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, null).expect("0"),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, null).expect("0"),
      Given(Field.DRUGS_INJECTED_STEROIDS, null).expect("0"),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, null).expect("0"),

      Given(Field.DRUGS_INJECTED_HEROIN, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_CRACK, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_COCAINE, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_STEROIDS, emptyList()).expect("0"),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, emptyList()).expect("0"),

      Given(Field.DRUGS_INJECTED_HEROIN, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_CRACK, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_COCAINE, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_STEROIDS, Value.LAST_SIX).expect("2"),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, Value.LAST_SIX).expect("2"),

      Given(Field.DRUGS_INJECTED_HEROIN, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_METHADONE_NOT_PRESCRIBED, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_OTHER_OPIATES, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_CRACK, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_COCAINE, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_MISUSED_PRESCRIBED_DRUGS, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_BENZODIAZEPINES, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_AMPHETAMINES, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_STEROIDS, Value.MORE_THAN_SIX).expect("1"),
      Given(Field.DRUGS_INJECTED_OTHER_DRUG, Value.MORE_THAN_SIX).expect("1"),

      Given(Field.DRUGS_INJECTED_HEROIN, Value.MORE_THAN_SIX)
        .and(Field.DRUGS_INJECTED_COCAINE, Value.LAST_SIX)
        .expect("2"),
    )
  }

  @Test
  fun q8() {
    test(
      "o8-8",
      Given().expect(null),
      Given(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP, null).expect(null),
      Given(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP, Value.FULL_MOTIVATION).expect("0"),
      Given(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP, Value.PARTIAL_MOTIVATION).expect("1"),
      Given(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP, Value.NO_MOTIVATION).expect("2"),
      Given(Field.DRUGS_PRACTITIONER_ANALYSIS_MOTIVATED_TO_STOP, Value.UNKNOWN).expect("M"),
    )
  }

  @Test
  fun q97() {
    test(
      "o8-97",
      *PractitionerAnalysisScenarios("DRUG_USE").notes(),
    )
  }

  @Test
  fun q98() {
    test(
      "o8-98",
      *PractitionerAnalysisScenarios("DRUG_USE").riskOfSeriousHarm(),
    )
  }

  @Test
  fun q99() {
    test(
      "o8-99",
      *PractitionerAnalysisScenarios("DRUG_USE").riskOfReoffending(),
    )
  }

  @Test
  fun qStrength() {
    test(
      "o8_SAN_STRENGTH",
      *PractitionerAnalysisScenarios("DRUG_USE").strengthsOrProtectiveFactors(),
    )
  }
}
