package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Option
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Field as FormField

val testFormConfig = FormConfig(
  "form-name",
  "1.0",
  mapOf(
    Field.CURRENT_ACCOMMODATION.lower to FormField(
      Field.CURRENT_ACCOMMODATION.lower,
      listOf(Option(Value.TEMPORARY.name), Option(Value.NO_ACCOMMODATION.name), Option(Value.SETTLED.name)),
    ),
    Field.SUITABLE_HOUSING.lower to FormField(
      Field.SUITABLE_HOUSING.lower,
      listOf(Option(Value.YES.name), Option(Value.YES_WITH_CONCERNS.name), Option(Value.NO.name)),
    ),
    Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING.lower to FormField(
      Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_REOFFENDING.lower,
    ),
    Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM.lower to FormField(
      Field.ACCOMMODATION_PRACTITIONER_ANALYSIS_RISK_OF_SERIOUS_HARM.lower,
    ),
  ),
)
