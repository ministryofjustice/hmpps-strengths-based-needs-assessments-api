package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception.InvalidMappingException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Option
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers

class Answer(
  val value: String?,
  val values: List<String>?,
)

class AnswersProvider(private val answers: Answers, private val config: FormConfig) {
  private var context: Field? = null

  fun answer(field: Field): Answer {
    context = field
    val fieldName = field.lower

    if (config.fields[fieldName]?.code != fieldName) {
      throw InvalidMappingException("Field $fieldName does not exist in form config version ${config.version}")
    }

    val answer = answers[fieldName]
    return Answer(answer?.value, answer?.values)
  }

  fun get(value: Value): String {
    if (context == null) {
      throw InvalidMappingException("Cannot obtain values without a field context. Call answer() first")
    }

    val fieldName = context!!.lower
    val valueName = value.name

    if (config.fields[fieldName]?.options?.contains(Option(valueName)) != true) {
      throw InvalidMappingException("$valueName is not a valid option for field $fieldName in form config version ${config.version}")
    }

    return valueName
  }
}
