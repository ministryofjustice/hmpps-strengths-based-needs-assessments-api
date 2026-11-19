package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = UpdateOasysDataMapping::class, name = "UpdateOasysDataMapping"),
)
sealed interface Hook

data class UpdateOasysDataMapping(
  val formConfig: FormConfig,
) : Hook

@JsonIgnoreProperties(ignoreUnknown = true)
data class FormConfig(
  val version: String,
  val fields: Map<String, Field> = emptyMap(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Field(
  val code: String,
  val options: List<Option> = emptyList(),
  val type: FieldType = FieldType.TEXT,
  val section: String = "",
  val collection: String = "",
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Option(
  val value: String? = null,
)

enum class FieldType {
  RADIO,
  CHECKBOX,
  TEXT,
  DATE,
  SELECT,
}
