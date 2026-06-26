package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.mappers

class QuestionCodeMapper {
  companion object {
    val overrides = mapOf<String, String>(
      // add overrides here
    )

    fun getCodeFor(questionCode: String) = overrides[questionCode] ?: questionCode
  }
}