package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Given
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.SectionMappingTest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer as PersistedAnswer

class OffenceAnalysisTest : SectionMappingTest(OffenceAnalysis(), "1.0") {
  @Test
  fun q1() {
    test(
      "o2-1",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_DESCRIPTION_OF_OFFENCE, "Test").expect("Test"),
    )
  }

  @Test
  fun q2Weapon() {
    test(
      "o2-2_V2_WEAPON",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.WEAPON)).expect("YES"),
    )
  }

  @Test
  fun q2ViolenceOrCoercion() {
    test(
      "o2-2_V2_ANYVIOL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.VIOLENCE_OR_COERCION)).expect("YES"),
    )
  }

  @Test
  fun q2Arson() {
    test(
      "o2-2_V2_ARSON",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.ARSON)).expect("YES"),
    )
  }

  @Test
  fun q2DomesticAbuse() {
    test(
      "o2-2_V2_DOM_ABUSE",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.DOMESTIC_ABUSE)).expect("YES"),
    )
  }

  @Test
  fun q2ExcessiveOrSadisticViolence() {
    test(
      "o2-2_V2_EXCESSIVE",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.EXCESSIVE_OR_SADISTIC_VIOLENCE)).expect("YES"),
    )
  }

  @Test
  fun q2PhysicalDamageToProperty() {
    test(
      "o2-2_V2_PHYSICALDAM",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.PHYSICAL_DAMAGE_TO_PROPERTY)).expect("YES"),
    )
  }

  @Test
  fun q2SexualElement() {
    test(
      "o2-2_V2_SEXUAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.SEXUAL_ELEMENT)).expect("YES"),
    )
  }

  @Test
  fun q3() {
    test(
      "o2-3",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, emptyList()).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.VICTIM_TARGETED)).expect("DIRECTCONT"),
      Given(Field.OFFENCE_ANALYSIS_ELEMENTS, listOf(Value.HATRED_OF_IDENTIFIABLE_GROUPS)).expect("HATE"),
      Given(
        Field.OFFENCE_ANALYSIS_ELEMENTS,
        listOf(Value.VICTIM_TARGETED, Value.HATRED_OF_IDENTIFIABLE_GROUPS),
      ).expect("DIRECTCONT,HATE"),
      Given.aCollectionOf(
        Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION,
        listOf(
          mapOf(
            Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP.lower to PersistedAnswer(
              type = AnswerType.RADIO,
              value = "STRANGER",
            ),
          ),
        ),
      ).expect("STRANGERS"),
      Given.aCollectionOf(
        Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION,
        listOf(
          mapOf(
            Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP.lower to PersistedAnswer(
              type = AnswerType.RADIO,
              value = "STRANGER",
            ),
          ),
        ),
      ).and(
        Field.OFFENCE_ANALYSIS_ELEMENTS,
        listOf(Value.VICTIM_TARGETED, Value.HATRED_OF_IDENTIFIABLE_GROUPS),
      ).expect("DIRECTCONT,HATE,STRANGERS"),
    )
  }

  @Test
  fun q6() {
    test(
      "o2-6",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, Value.YES).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_IMPACT_ON_VICTIMS, Value.NO).expect("NO"),
    )
  }

  @Test
  fun q7() {
    test(
      "o2-7",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.NONE).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.ONE).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.TWO).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.THREE).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.FOUR).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.FIVE).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.SIX_TO_10).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.ELEVEN_TO_15).expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.MORE_THAN_15).expect("YES"),
    )
  }

  @Test
  fun q71() {
    test(
      "o2-7-1",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.NONE).expect(null),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.ONE).expect("110"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.TWO).expect("120"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.THREE).expect("130"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.FOUR).expect("140"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.FIVE).expect("150"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.SIX_TO_10).expect("160"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.ELEVEN_TO_15).expect("170"),
      Given(Field.OFFENCE_ANALYSIS_HOW_MANY_INVOLVED, Value.MORE_THAN_15).expect("180"),
    )
  }

  @Test
  fun q72() {
    test(
      "o2-7-2",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.PRESSURISED_BY_OTHERS)).expect("YES"),
    )
  }

  @Test
  fun q73() {
    test(
      "o2-7-3",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_LEADER, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_LEADER, Value.NO).expect("No"),
      Given(Field.OFFENCE_ANALYSIS_LEADER, Value.NO)
        .and(Field.OFFENCE_ANALYSIS_LEADER_NO_DETAILS, "some details")
        .expect("No - some details"),
      Given(Field.OFFENCE_ANALYSIS_LEADER, Value.YES).expect("Yes"),
      Given(Field.OFFENCE_ANALYSIS_LEADER, Value.YES)
        .and(Field.OFFENCE_ANALYSIS_LEADER_YES_DETAILS, "some details")
        .expect("Yes - some details"),
    )
  }

  @Test
  fun q8() {
    test(
      "o2-8",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_REASON, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_REASON, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_REASON, "Some details")
        .expect("Some details"),
    )
  }

  @Test
  fun q9SexualMotivations() {
    test(
      "o2-9_V2_SEXUAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.SEXUAL_MOTIVATION)).expect("YES"),
    )
  }

  @Test
  fun q9FinancialMotivations() {
    test(
      "o2-9_V2_FINANCIAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.FINANCIAL_MOTIVATION)).expect("YES"),
    )
  }

  @Test
  fun q9AddictionMotivations() {
    test(
      "o2-9_V2_ADDICTION",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.ADDICTIONS_OR_PERCEIVED_NEEDS)).expect("YES"),
    )
  }

  @Test
  fun q9EmotionalMotivations() {
    test(
      "o2-9_V2_EMOTIONAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.EMOTIONAL_STATE)).expect("YES"),
    )
  }

  @Test
  fun q9RacialMotivations() {
    test(
      "o2-9_V2_RACIAL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.HATRED_OF_IDENTIFIABLE_GROUPS)).expect("YES"),
    )
  }

  @Test
  fun q29ThrillMotivations() {
    test(
      "o2-9_V2_THRILL",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.THRILL_SEEKING)).expect("YES"),
    )
  }

  @Test
  fun q29OtherMotivations() {
    test(
      "o2-9_V2_OTHER",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, emptyList()).expect("NO"),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS, listOf(Value.OTHER)).expect("YES"),
    )
  }

  @Test
  fun q29t() {
    test(
      "o2-9-t_V2",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS_OTHER_DETAILS, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS_OTHER_DETAILS, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_MOTIVATIONS_OTHER_DETAILS, "Some details").expect("Some details"),
    )
  }

  @Test
  fun q11() {
    test(
      "o2-11",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, "YES").expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, "NO").expect("NO"),
    )
  }

  @Test
  fun q11t() {
    test(
      "o2-11-t",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, "YES")
        .and(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_YES_DETAILS, "Some details")
        .expect("Some details"),
      Given(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY, "NO")
        .and(Field.OFFENCE_ANALYSIS_ACCEPT_RESPONSIBILITY_NO_DETAILS, "Some details")
        .expect("Some details"),
    )
  }

  @Test
  fun q12() {
    test(
      "o2-12",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, "").expect(""),
      Given(Field.OFFENCE_ANALYSIS_PATTERNS_OF_OFFENDING, "Some details").expect("Some details"),
    )
  }

  @Test
  fun q13() {
    test(
      "o2-13",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_ESCALATION, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_ESCALATION, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_ESCALATION, "YES").expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_ESCALATION, "NO").expect("NO"),
    )
  }

  @Test
  fun q98() {
    test(
      "o2-98",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, "YES")
        .and(Field.OFFENCE_ANALYSIS_RISK_YES_DETAILS, "Some details")
        .expect("Some details"),
      Given(Field.OFFENCE_ANALYSIS_RISK, "NO")
        .and(Field.OFFENCE_ANALYSIS_RISK_NO_DETAILS, "Some details")
        .expect("Some details"),
    )
  }

  @Test
  fun q99() {
    test(
      "o2-99",
      Given().expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, null).expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, "").expect(null),
      Given(Field.OFFENCE_ANALYSIS_RISK, "YES").expect("YES"),
      Given(Field.OFFENCE_ANALYSIS_RISK, "NO").expect("NO"),
    )
  }

  @Test
  fun victimAge() {
    mapOf(
      Value.AGE_0_TO_4_YEARS.name to "0",
      Value.AGE_5_TO_11_YEARS.name to "1",
      Value.AGE_12_TO_15_YEARS.name to "2",
      Value.AGE_16_TO_17_YEARS.name to "3",
      Value.AGE_18_TO_20_YEARS.name to "4",
      Value.AGE_21_TO_25_YEARS.name to "5",
      Value.AGE_26_TO_49_YEARS.name to "6",
      Value.AGE_50_TO_64_YEARS.name to "7",
      Value.AGE_65_AND_OVER.name to "8",
    ).forEach { (givenValue, expectedValue) ->
      val entries: List<Map<String, PersistedAnswer>> = listOf(
        mapOf(
          Field.OFFENCE_ANALYSIS_VICTIM_AGE.lower to PersistedAnswer(
            type = AnswerType.RADIO,
            value = givenValue,
          ),
        ),
      )

      val expectedEntry = mapOf(
        "oAGE_OF_VICTIM_ELM" to expectedValue,
        "oGENDER_ELM" to null,
        "oETHNIC_CATEGORY_ELM" to null,
        "oVICTIM_RELATION_ELM" to null,
      )

      test(
        "victim0",
        Given.aCollectionOf(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION, entries).expect(expectedEntry),
      )
    }
  }

  @Test
  fun victimGender() {
    mapOf(
      Value.MALE.name to "1",
      Value.FEMALE.name to "2",
      Value.INTERSEX.name to null,
      Value.UNKNOWN.name to "0",
    ).forEach { (givenValue, expectedValue) ->
      val entries: List<Map<String, PersistedAnswer>> = listOf(
        mapOf(
          Field.OFFENCE_ANALYSIS_VICTIM_SEX.lower to PersistedAnswer(
            type = AnswerType.RADIO,
            value = givenValue,
          ),
        ),
      )

      val expectedEntry = mapOf(
        "oAGE_OF_VICTIM_ELM" to null,
        "oGENDER_ELM" to expectedValue,
        "oETHNIC_CATEGORY_ELM" to null,
        "oVICTIM_RELATION_ELM" to null,
      )

      test(
        "victim0",
        Given.aCollectionOf(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION, entries).expect(expectedEntry),
      )
    }
  }

  @Test
  fun victimRace() {
    mapOf(
      Value.WHITE_ENGLISH_WELSH_SCOTTISH_NORTHERN_IRISH_OR_BRITISH.name to "W1",
      Value.WHITE_IRISH.name to "W2",
      Value.WHITE_GYPSY_OR_IRISH_TRAVELLER.name to "W4",
      Value.WHITE_ANY_OTHER_WHITE_BACKGROUND.name to "W9",
      Value.MIXED_WHITE_AND_BLACK_CARIBBEAN.name to "M1",
      Value.MIXED_WHITE_AND_BLACK_AFRICAN.name to "M2",
      Value.MIXED_WHITE_AND_ASIAN.name to "M3",
      Value.MIXED_ANY_OTHER_MIXED_OR_MULTIPLE_ETHNIC_BACKGROUND_BACKGROUND.name to "M9",
      Value.ASIAN_OR_ASIAN_BRITISH_INDIAN.name to "A1",
      Value.ASIAN_OR_ASIAN_BRITISH_PAKISTANI.name to "A2",
      Value.ASIAN_OR_ASIAN_BRITISH_BANGLADESHI.name to "A3",
      Value.ASIAN_OR_ASIAN_BRITISH_CHINESE.name to "A4",
      Value.ASIAN_OR_ASIAN_BRITISH_ANY_OTHER_ASIAN_BACKGROUND.name to "A9",
      Value.BLACK_OR_BLACK_BRITISH_CARIBBEAN.name to "B1",
      Value.BLACK_OR_BLACK_BRITISH_AFRICAN.name to "B2",
      Value.BLACK_OR_BLACK_BRITISH_ANY_OTHER_BLACK_BACKGROUND.name to "B9",
      Value.ARAB.name to "O2",
      Value.ANY_OTHER_ETHNIC_GROUP.name to "O9",
    ).forEach { (givenValue, expectedValue) ->
      val entries: List<Map<String, PersistedAnswer>> = listOf(
        mapOf(
          Field.OFFENCE_ANALYSIS_VICTIM_RACE.lower to PersistedAnswer(
            type = AnswerType.RADIO,
            value = givenValue,
          ),
        ),
      )

      val expectedEntry = mapOf(
        "oAGE_OF_VICTIM_ELM" to null,
        "oGENDER_ELM" to null,
        "oETHNIC_CATEGORY_ELM" to expectedValue,
        "oVICTIM_RELATION_ELM" to null,
      )

      test(
        "victim0",
        Given.aCollectionOf(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION, entries).expect(expectedEntry),
      )
    }
  }

  @Test
  fun victimRelationship() {
    mapOf(
      Value.STRANGER.name to "0",
      Value.CRIMINAL_JUSTICE_STAFF.name to "12",
      Value.POP_PARENT_OR_STEP_PARENT.name to "14",
      Value.POP_EX_PARTNER.name to "15",
      Value.POP_CHILD_OR_STEP_CHILD.name to "5",
      Value.OTHER_FAMILY_MEMBER.name to "6",
      Value.POP_PARTNER.name to "1",
      Value.OTHER.name to "13",
    ).forEach { (givenValue, expectedValue) ->
      val entries: List<Map<String, PersistedAnswer>> = listOf(
        mapOf(
          Field.OFFENCE_ANALYSIS_VICTIM_RELATIONSHIP.lower to PersistedAnswer(
            type = AnswerType.RADIO,
            value = givenValue,
          ),
        ),
      )

      val expectedEntry = mapOf(
        "oAGE_OF_VICTIM_ELM" to null,
        "oGENDER_ELM" to null,
        "oETHNIC_CATEGORY_ELM" to null,
        "oVICTIM_RELATION_ELM" to expectedValue,
      )

      test(
        "victim0",
        Given.aCollectionOf(Field.OFFENCE_ANALYSIS_VICTIMS_COLLECTION, entries).expect(expectedEntry),
      )
    }
  }
}
