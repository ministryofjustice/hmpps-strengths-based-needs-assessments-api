package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.mappers

val yesNoToTrueFalse = mapOf(
  "YES" to "TRUE",
  "NO" to "FALSE",
)

class AnswerValueMapper {
  companion object {
    val overrides = mapOf(
      "accommodation_is_current_accommodation_user_submitted" to yesNoToTrueFalse,
      "accommodation_is_settled_accommodation_user_submitted" to yesNoToTrueFalse,
      "accommodation_is_temporary_accommodation_user_submitted" to yesNoToTrueFalse,
      "accommodation_is_temporary_accommodation_cas_ap_user_submitted" to yesNoToTrueFalse,
      "accommodation_is_no_accommodation_user_submitted" to yesNoToTrueFalse,
      "accommodation_is_accommodation_summary_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_current_employment_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_employed_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_retired_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_employed_before_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_never_employed_user_submitted" to yesNoToTrueFalse,
      "employment_education_is_employment_education_summary_user_submitted" to yesNoToTrueFalse,
      "finance_is_finance_user_submitted" to yesNoToTrueFalse,
      "finance_is_finance_summary_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_use_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_add_drugs_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_details_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_details_injected_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_details_more_than_six_months_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_details_more_than_six_months_injected_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_use_history_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_use_history_more_than_six_months_user_submitted" to yesNoToTrueFalse,
      "drug_use_is_drug_use_summary_user_submitted" to yesNoToTrueFalse,
      "alcohol_use_is_alcohol_user_submitted" to yesNoToTrueFalse,
      "alcohol_use_is_alcohol_use_last_three_months_user_submitted" to yesNoToTrueFalse,
      "alcohol_use_is_alcohol_use_less_three_months_user_submitted" to yesNoToTrueFalse,
      "alcohol_use_is_alcohol_use_summary_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_health_wellbeing_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_physical_mental_health_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_physical_health_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_mental_health_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_no_physical_mental_health_user_submitted" to yesNoToTrueFalse,
      "health_wellbeing_is_health_wellbeing_summary_user_submitted" to yesNoToTrueFalse,
      "thinking_behaviours_attitudes_is_thinking_behaviours_attitudes_user_submitted" to yesNoToTrueFalse,
      "thinking_behaviours_attitudes_is_thinking_behaviours_attitudes_risk_of_sexual_harm_user_submitted" to yesNoToTrueFalse,
      "thinking_behaviours_attitudes_is_thinking_behaviours_attitudes_risk_of_sexual_harm_details_user_submitted" to yesNoToTrueFalse,
      "thinking_behaviours_attitudes_is_thinking_behaviours_attitudes_summary_user_submitted" to yesNoToTrueFalse,
      "personal_relationships_community_is_personal_relationships_children_information_user_submitted" to yesNoToTrueFalse,
      "personal_relationships_community_is_personal_relationships_user_submitted" to yesNoToTrueFalse,
      "personal_relationships_community_is_personal_relationships_community_user_submitted" to yesNoToTrueFalse,
      "personal_relationships_community_is_personal_relationships_children_user_submitted" to yesNoToTrueFalse,
      "personal_relationships_community_is_personal_relationships_community_summary_user_submitted" to yesNoToTrueFalse,
      "offence_analysis_is_offence_analysis_user_submitted" to yesNoToTrueFalse,
      "offence_analysis_is_offence_analysis_victim_details_user_submitted" to yesNoToTrueFalse,
      "offence_analysis_is_offence_analysis_involved_parties_user_submitted" to yesNoToTrueFalse,
      "offence_analysis_is_offence_analysis_impact_user_submitted" to yesNoToTrueFalse,
      "offence_analysis_is_offence_analysis_impact_others_involved_user_submitted" to yesNoToTrueFalse,
    )

    fun getValueFor(originalQuestionCode: String, originalValue: String) = overrides[originalQuestionCode]?.get(originalValue) ?: originalValue
  }
}
