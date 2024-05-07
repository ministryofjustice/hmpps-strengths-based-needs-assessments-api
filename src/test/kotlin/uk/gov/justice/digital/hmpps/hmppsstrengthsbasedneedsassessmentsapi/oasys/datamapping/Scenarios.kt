package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.v1.testFormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import kotlin.test.assertContains
import kotlin.test.assertEquals

class Scenarios(private vararg val scenarios: Given) {
  fun test(sectionMapping: SectionMapping, questionCode: String) {
    for (scenario in scenarios) {
      val answersProvider = AnswersProvider(scenario.answers, testFormConfig)
      val result = sectionMapping.map(answersProvider)

      assertContains(result, questionCode)
      assertEquals(scenario.expected, result[questionCode])
    }
  }
}

class Given {
  var answers: Answers = emptyMap()
  lateinit var expected: Any

  constructor()

  constructor(field: Field, value: String?) {
    this.answers = answers + mapOf(field.lower to Answer(value = value))
  }

  constructor(field: Field, value: Value) {
    this.answers = answers + mapOf(field.lower to Answer(value = value.name))
  }

  constructor(field: Field, values: List<String>) {
    this.answers = answers + mapOf(field.lower to Answer(values = values))
  }

  fun and(field: Field, value: String?): Given {
    this.answers = answers + mapOf(field.lower to Answer(value = value))
    return this
  }

  fun and(field: Field, value: Value): Given {
    this.answers = answers + mapOf(field.lower to Answer(value = value.name))
    return this
  }

  fun and(field: Field, values: List<String>): Given {
    this.answers = answers + mapOf(field.lower to Answer(values = values))
    return this
  }

  fun expect(expected: Any): Given {
    this.expected = expected
    return this
  }
}
