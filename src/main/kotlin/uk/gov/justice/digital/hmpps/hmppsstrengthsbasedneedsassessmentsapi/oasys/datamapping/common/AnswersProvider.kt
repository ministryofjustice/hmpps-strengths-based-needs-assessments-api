package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Option
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.exception.InvalidMappingException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class Answer(
  val value: String?,
  val values: List<String>?,
)

class AnswersProvider(
  private val answers: Answers,
  private val config: FormConfig,
) {
  private var context: String? = null

  fun answer(field: Field): Answer {
    context = config.fields[field.lower]?.let { field.lower }
      ?: config.fields.entries.find { it.value.code == field.lower }?.key

    return config.fields[context]?.code?.let {
      val answer = answers[it]
      Answer(answer?.value, answer?.values)
    } ?: throw InvalidMappingException("Field ${field.lower} does not exist in form config version ${config.version}")
  }

  fun get(value: Value): String {
    if (context == null) {
      throw InvalidMappingException("Cannot obtain values without a field context. Call answer() first")
    }

    val valueName = value.name

    if (config.fields[context]?.options?.contains(Option(valueName)) != true) {
      throw InvalidMappingException("$valueName is not a valid option for field $context in form config version ${config.version}")
    }

    return valueName
  }
}
