package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.mappers

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.MultiValue
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.SingleValue
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType

class AnswerMapper {
  companion object {
    fun isSectionStatusCode(key: String) = key.matches(
      Regex(
        """^(?:[A-Za-z0-9_]+_(?:background_section_complete|practitioner_analysis_section_complete|section_complete)|assessment_complete)$""",
      ),
    )

    fun isCollection(pair: Map.Entry<String, Answer>) = pair.let { (_, answer) -> answer.type === AnswerType.COLLECTION }
    fun isProperty(pair: Map.Entry<String, Answer>) = pair.let { (key, _) -> isSectionStatusCode(key) }

    fun toAapValue(pair: Map.Entry<String, Answer>): Pair<String, Value> = pair.let { (key, answer) ->
      val aapAnswerCode = QuestionCodeMapper.getCodeFor(key)
      aapAnswerCode to when (answer.type) {
        AnswerType.CHECKBOX -> MultiValue(answer.values.orEmpty().map(AnswerValueMapper::getCodeFor))
        else -> SingleValue(AnswerValueMapper.getCodeFor(answer.value.orEmpty()))
      }
    }
  }
}
