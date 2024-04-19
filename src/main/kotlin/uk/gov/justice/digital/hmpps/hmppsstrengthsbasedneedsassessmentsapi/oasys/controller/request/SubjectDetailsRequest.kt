package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Location

const val OASYS_GENDER_NOT_KNOWN = 0
const val OASYS_GENDER_MALE = 1
const val OASYS_GENDER_FEMALE = 2
const val OASYS_GENDER_NOT_SPECIFIED = 9

enum class OasysGender(val oasysValue: Int) {
  NOT_KNOWN(OASYS_GENDER_NOT_KNOWN),
  MALE(OASYS_GENDER_MALE),
  FEMALE(OASYS_GENDER_FEMALE),
  NOT_SPECIFIED(OASYS_GENDER_NOT_SPECIFIED),
}

data class SubjectDetailsRequest(
  val crn: String?,
  val pnc: String?,
  val nomisId: String?,
  val givenName: String,
  val familyName: String,
  val gender: OasysGender,
  val location: Location,
  val sexuallyMotivatedOffenceHistory: String?,
)
