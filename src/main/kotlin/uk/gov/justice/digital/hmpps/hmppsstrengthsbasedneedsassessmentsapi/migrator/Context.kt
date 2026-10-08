package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answers
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment

data class Context(
  val assessment: Assessment,
  val assessmentUuid: String,
  val victimsCollectionUuid: String,
  val victimsCollection: MutableSet<String> = mutableSetOf(),
  var previousVersion: Int = 0,
  var migrationCommands: Int = 0,
  var versionsMigrated: Int = 0,
  var previousAnswers: Answers = emptyMap(),
  var previousProperties: Answers = emptyMap(),
)
