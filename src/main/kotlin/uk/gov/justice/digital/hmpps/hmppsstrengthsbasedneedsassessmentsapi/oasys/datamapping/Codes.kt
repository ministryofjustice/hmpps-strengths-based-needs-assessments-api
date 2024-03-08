package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping

enum class Field {
  ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING,
  ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM,
  CURRENT_ACCOMMODATION,
  SUITABLE_HOUSING,
  TEST_FIELD,
  ;

  val lower = name.lowercase()
}

enum class Value {
  NO,
  NO_ACCOMMODATION,
  SETTLED,
  TEMPORARY,
  YES,
  YES_WITH_CONCERNS,
}
