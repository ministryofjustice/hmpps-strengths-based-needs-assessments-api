package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.OASysYesNo
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.OasysGender
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SubjectDetailsRequest

enum class Gender {
  NOT_KNOWN,
  MALE,
  FEMALE,
  NOT_SPECIFIED,
  ;

  companion object {
    fun from(value: OasysGender): Gender {
      return when (value) {
        OasysGender.NOT_KNOWN -> NOT_KNOWN
        OasysGender.MALE -> MALE
        OasysGender.FEMALE -> FEMALE
        OasysGender.NOT_SPECIFIED -> NOT_SPECIFIED
      }
    }
  }
}

enum class Location {
  PRISON,
  COMMUNITY,
}

data class SubjectDetails(
  val crn: String?,
  val pnc: String?,
  val nomisId: String?,
  val givenName: String,
  val familyName: String,
  val gender: Gender,
  val location: Location,
  val sexuallyMotivatedOffenceHistory: Boolean?,
) {
  companion object {
    private fun sexuallyMotivatedOffenceHistoryFrom(value: OASysYesNo?): Boolean? {
      return when (value) {
        OASysYesNo.YES -> true
        OASysYesNo.NO -> false
        else -> null
      }
    }

    fun from(request: SubjectDetailsRequest): SubjectDetails {
      return SubjectDetails(
        request.crn,
        request.pnc,
        request.nomisId,
        request.givenName,
        request.familyName,
        Gender.from(request.gender),
        request.location,
        sexuallyMotivatedOffenceHistoryFrom(request.sexuallyMotivatedOffenceHistory),
      )
    }
  }
}
