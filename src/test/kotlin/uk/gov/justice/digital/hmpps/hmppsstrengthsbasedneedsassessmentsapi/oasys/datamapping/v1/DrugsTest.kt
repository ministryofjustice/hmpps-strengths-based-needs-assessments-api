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
      Given(Field.DRUG_USAGE_HEROIN, null).expect(null),
      Given(Field.DRUG_USAGE_HEROIN, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_HEROIN, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_HEROIN, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_HEROIN, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2013() {
    test(
      "o8-2-1-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_HEROIN, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_HEROIN, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_HEROIN, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2012() {
    test(
      "o8-2-1-2",
      Given().expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, null).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, Value.NO).expect(null),
      Given(Field.DRUGS_INJECTED_HEROIN, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2014() {
    test(
      "o8-2-1-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_HEROIN, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_HEROIN, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_HEROIN, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2021() {
    test(
      "o8-2-2-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2023() {
    test(
      "o8-2-2-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2022() {
    test(
      "o8-2-2-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2024() {
    test(
      "o8-2-2-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2031() {
    test(
      "o8-2-3-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, null).expect(null),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2033() {
    test(
      "o8-2-3-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_OPIATES, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_OPIATES, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_OPIATES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2032() {
    test(
      "o8-2-3-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, null).expect(null),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2034() {
    test(
      "o8-2-3-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2041() {
    test(
      "o8-2-4-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_CRACK, null).expect(null),
      Given(Field.DRUG_USAGE_CRACK, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_CRACK, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_CRACK, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_CRACK, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2043() {
    test(
      "o8-2-4-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_CRACK, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_CRACK, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_CRACK, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2042() {
    test(
      "o8-2-4-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_CRACK, null).expect(null),
      Given(Field.INJECTING_DRUG_CRACK, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_CRACK, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2044() {
    test(
      "o8-2-4-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_CRACK, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_CRACK, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_CRACK, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2051() {
    test(
      "o8-2-5-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_COCAINE, null).expect(null),
      Given(Field.DRUG_USAGE_COCAINE, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_COCAINE, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_COCAINE, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_COCAINE, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2053() {
    test(
      "o8-2-5-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_COCAINE, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_COCAINE, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_COCAINE, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2052() {
    test(
      "o8-2-5-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_COCAINE, null).expect(null),
      Given(Field.INJECTING_DRUG_COCAINE, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_COCAINE, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2054() {
    test(
      "o8-2-5-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2061() {
    test(
      "o8-2-6-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2063() {
    test(
      "o8-2-6-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2062() {
    test(
      "o8-2-6-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2064() {
    test(
      "o8-2-6-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2071() {
    test(
      "o8-2-7-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, null).expect(null),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2073() {
    test(
      "o8-2-7-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_BENZODIAZEPINES, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_BENZODIAZEPINES, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_BENZODIAZEPINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2072() {
    test(
      "o8-2-7-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, null).expect(null),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2074() {
    test(
      "o8-2-7-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2081() {
    test(
      "o8-2-8-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_AMPHETAMINES, null).expect(null),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2083() {
    test(
      "o8-2-8-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_AMPHETAMINES, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_AMPHETAMINES, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_AMPHETAMINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2082() {
    test(
      "o8-2-8-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, null).expect(null),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2084() {
    test(
      "o8-2-8-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2091() {
    test(
      "o8-2-9-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, null).expect(null),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2093() {
    test(
      "o8-2-9-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_HALLUCINOGENICS, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_HALLUCINOGENICS, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_HALLUCINOGENICS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2101() {
    test(
      "o8-2-10-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_ECSTASY, null).expect(null),
      Given(Field.DRUG_USAGE_ECSTASY, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2103() {
    test(
      "o8-2-10-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_ECSTASY, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_ECSTASY, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_ECSTASY, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2111() {
    test(
      "o8-2-11-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_CANNABIS, null).expect(null),
      Given(Field.DRUG_USAGE_CANNABIS, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2113() {
    test(
      "o8-2-11-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_CANNABIS, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_CANNABIS, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_CANNABIS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2121() {
    test(
      "o8-2-12-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_SOLVENTS, null).expect(null),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2123() {
    test(
      "o8-2-12-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_SOLVENTS, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_SOLVENTS, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_SOLVENTS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2131() {
    test(
      "o8-2-13-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_STEROIDS, null).expect(null),
      Given(Field.DRUG_USAGE_STEROIDS, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2133() {
    test(
      "o8-2-13-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_STEROIDS, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_STEROIDS, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_STEROIDS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2132() {
    test(
      "o8-2-13-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_STEROIDS, null).expect(null),
      Given(Field.INJECTING_DRUG_STEROIDS, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_STEROIDS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2134() {
    test(
      "o8-2-13-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2151() {
    test(
      "o8-2-15-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_SPICE, null).expect(null),
      Given(Field.DRUG_USAGE_SPICE, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_SPICE, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_SPICE, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_SPICE, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2153() {
    test(
      "o8-2-15-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_SPICE, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_SPICE, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_SPICE, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2141() {
    test(
      "o8-2-14-1",
      Given().expect(null),
      Given(Field.DRUG_USAGE_OTHER_DRUG, null).expect(null),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.DAILY).expect("100"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.WEEKLY).expect("110"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.MONTHLY).expect("120"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.OCCASIONALLY).expect("130"),
    )
  }

  @Test
  fun q2143() {
    test(
      "o8-2-14-3",
      Given().expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_DRUG, null).expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_DRUG, Value.NO).expect(null),
      Given(Field.PAST_DRUG_USAGE_OTHER_DRUG, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2142() {
    test(
      "o8-2-14-2",
      Given().expect(null),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, null).expect(null),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, Value.NO).expect(null),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q2144() {
    test(
      "o8-2-14-4",
      Given().expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, null).expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, Value.NO).expect(null),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, Value.YES).expect("YES"),
    )
  }

  @Test
  fun q214t() {
    test(
      "o8-2-14-t",
      Given().expect(null),
      Given(Field.DRUG_USE_TYPE_OTHER_DRUG_DETAILS, null).expect(null),
      Given(Field.DRUG_USE_TYPE_OTHER_DRUG_DETAILS, "").expect(""),
      Given(Field.DRUG_USE_TYPE_OTHER_DRUG_DETAILS, "some text").expect("some text"),
    )
  }

  @Test
  fun q4() {
    test(
      "o8-4",
      Given().expect("0"),
      Given(Field.DRUG_USAGE_HEROIN, null).expect("0"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, null).expect("0"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, null).expect("0"),
      Given(Field.DRUG_USAGE_CRACK, null).expect("0"),
      Given(Field.DRUG_USAGE_COCAINE, null).expect("0"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, null).expect("0"),
      Given(Field.DRUG_USAGE_HEROIN, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_HEROIN, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_HEROIN, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("2"),
      Given(Field.DRUG_USAGE_HEROIN, Value.OCCASIONALLY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.OCCASIONALLY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.OCCASIONALLY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.OCCASIONALLY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("2"),
    )
  }

  @Test
  fun q5() {
    test(
      "o8-5",
      Given().expect("M"),
      Given(Field.DRUG_USAGE_HEROIN, null).expect("M"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, null).expect("M"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, null).expect("M"),
      Given(Field.DRUG_USAGE_CRACK, null).expect("M"),
      Given(Field.DRUG_USAGE_COCAINE, null).expect("M"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, null).expect("M"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, null).expect("M"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, null).expect("M"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, null).expect("M"),
      Given(Field.DRUG_USAGE_ECSTASY, null).expect("M"),
      Given(Field.DRUG_USAGE_CANNABIS, null).expect("M"),
      Given(Field.DRUG_USAGE_SOLVENTS, null).expect("M"),
      Given(Field.DRUG_USAGE_STEROIDS, null).expect("M"),
      Given(Field.DRUG_USAGE_SPICE, null).expect("M"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, null).expect("M"),

      Given(Field.DRUG_USAGE_HEROIN, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_SPICE, Value.DAILY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.DAILY).expect("2"),

      Given(Field.DRUG_USAGE_HEROIN, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_CRACK, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_COCAINE, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_SPICE, Value.WEEKLY).expect("2"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.WEEKLY).expect("2"),

      Given(Field.DRUG_USAGE_HEROIN, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_CRACK, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_COCAINE, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_SPICE, Value.MONTHLY).expect("0"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.MONTHLY).expect("0"),

      Given(Field.DRUG_USAGE_HEROIN, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_METHADONE_NOT_PRESCRIBED, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_OTHER_OPIATES, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_CRACK, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_COCAINE, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_MISUSED_PRESCRIBED_DRUGS, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_BENZODIAZEPINES, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_AMPHETAMINES, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_HALLUCINOGENICS, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_ECSTASY, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_CANNABIS, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_SOLVENTS, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_STEROIDS, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_SPICE, Value.OCCASIONALLY).expect("0"),
      Given(Field.DRUG_USAGE_OTHER_DRUG, Value.OCCASIONALLY).expect("0"),

      Given(Field.DRUG_USAGE_HEROIN, Value.DAILY)
        .and(Field.DRUG_USAGE_COCAINE, Value.MONTHLY)
        .expect("2"),
    )
  }

  @Test
  fun q6() {
    test(
      "o8-6",
      Given().expect("0"),

      Given(Field.INJECTING_DRUG_HEROIN, null).expect("0"),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, null).expect("0"),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, null).expect("0"),
      Given(Field.INJECTING_DRUG_CRACK, null).expect("0"),
      Given(Field.INJECTING_DRUG_COCAINE, null).expect("0"),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, null).expect("0"),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, null).expect("0"),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, null).expect("0"),
      Given(Field.INJECTING_DRUG_STEROIDS, null).expect("0"),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, null).expect("0"),

      Given(Field.INJECTING_DRUG_HEROIN, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_CRACK, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_COCAINE, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_STEROIDS, Value.NO).expect("0"),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, Value.NO).expect("0"),

      Given(Field.INJECTING_DRUG_HEROIN, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_OTHER_OPIATES, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_CRACK, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_COCAINE, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_BENZODIAZEPINES, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_AMPHETAMINES, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_STEROIDS, Value.YES).expect("2"),
      Given(Field.INJECTING_DRUG_OTHER_DRUG, Value.YES).expect("2"),

      Given(Field.PAST_INJECTING_DRUG_HEROIN, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_CRACK, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, null).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, null).expect("0"),

      Given(Field.PAST_INJECTING_DRUG_HEROIN, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_CRACK, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, Value.NO).expect("0"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, Value.NO).expect("0"),

      Given(Field.PAST_INJECTING_DRUG_HEROIN, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_METHADONE_NOT_PRESCRIBED, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_OPIATES, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_CRACK, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_COCAINE, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_MISUSED_PRESCRIBED_DRUGS, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_BENZODIAZEPINES, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_AMPHETAMINES, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_STEROIDS, Value.YES).expect("1"),
      Given(Field.PAST_INJECTING_DRUG_OTHER_DRUG, Value.YES).expect("1"),

      Given(Field.PAST_INJECTING_DRUG_HEROIN, Value.YES)
        .and(Field.INJECTING_DRUG_COCAINE, Value.YES)
        .expect("2"),
    )
  }

  @Test
  fun q8() {
    test(
      "o8-8",
      Given().expect(null),
      Given(Field.MOTIVATED_STOPPING_DRUG_USE, null).expect(null),
      Given(Field.MOTIVATED_STOPPING_DRUG_USE, Value.MOTIVATED).expect("0"),
      Given(Field.MOTIVATED_STOPPING_DRUG_USE, Value.SOME_MOTIVATION).expect("1"),
      Given(Field.MOTIVATED_STOPPING_DRUG_USE, Value.NO_MOTIVATION).expect("2"),
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
