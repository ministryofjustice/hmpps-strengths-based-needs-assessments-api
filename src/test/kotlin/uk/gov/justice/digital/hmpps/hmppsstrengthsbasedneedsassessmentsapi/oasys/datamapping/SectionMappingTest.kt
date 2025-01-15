package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping

import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.utils.IntegrationTest
import kotlin.test.assertContains
import kotlin.test.assertEquals

abstract class SectionMappingTest(
  private val sectionMapping: SectionMapping,
  private val version: String,
) : IntegrationTest() {
  @Autowired
  private lateinit var formConfigProvider: FormConfigProvider

  private val formConfig: FormConfig by lazy {
    val formInfo = AssessmentFormInfo(formVersion = version)
    formConfigProvider.get(formInfo)
  }

  fun test(questionCode: String, vararg scenarios: Given) {
    for ((scenarioNumber, scenario) in scenarios.withIndex()) {
      val answersProvider = AnswersProvider(scenario.answers, formConfig)
      val result = sectionMapping.map(answersProvider)

      assertContains(result, questionCode, "Scenario ${scenarioNumber + 1} failed")
      assertEquals(scenario.expected, result[questionCode], "Scenario ${scenarioNumber + 1} failed")
    }
  }
}

class Given {
  var answers: Answers = emptyMap()
  var expected: Any? = null

  constructor()

  constructor(field: Field, value: String?) {
    this.answers = answers + mapOf(field.lower to Answer(value = value))
  }

  constructor(field: Field, value: Value) {
    this.answers = answers + mapOf(field.lower to Answer(value = value.name))
  }

  constructor(field: Field, values: List<Value>) {
    this.answers = answers + mapOf(field.lower to Answer(values = values.map { it.name }))
  }

  fun and(field: Field, value: String?): Given {
    this.answers = answers + mapOf(field.lower to Answer(value = value))
    return this
  }

  fun and(field: Field, value: Value): Given {
    this.answers = answers + mapOf(field.lower to Answer(value = value.name))
    return this
  }

  fun and(field: Field, values: List<Value>): Given {
    this.answers = answers + mapOf(field.lower to Answer(values = values.map { it.name }))
    return this
  }

  fun expect(expected: Any?): Given {
    this.expected = expected
    return this
  }

  companion object {
    fun aCollectionOf(field: Field, collection: List<Map<String, Answer>>): Given = Given().apply { answers = mapOf(field.lower to Answer(collection = collection)) }
  }
}
