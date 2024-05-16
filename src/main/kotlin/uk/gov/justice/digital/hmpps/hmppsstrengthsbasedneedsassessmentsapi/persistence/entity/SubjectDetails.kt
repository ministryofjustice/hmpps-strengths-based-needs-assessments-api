package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import com.fasterxml.jackson.annotation.JsonFormat
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SubjectDetailsRequest
import java.time.LocalDate

enum class Gender {
  NOT_KNOWN,
  MALE,
  FEMALE,
  NOT_SPECIFIED,
  ;

  companion object {
    fun from(oasysValue: Int): Gender {
      return when (oasysValue) {
        0 -> NOT_KNOWN
        1 -> MALE
        2 -> FEMALE
        9 -> NOT_SPECIFIED
        else -> throw IllegalArgumentException("Invalid value for gender: $oasysValue")
      }
    }
  }
}

enum class Location {
  PRISON,
  COMMUNITY,
}

data class SubjectDetails(
  val crn: String? = null,
  val pnc: String? = null,
  val nomisId: String? = null,
  val givenName: String = "",
  val familyName: String = "",
  @JsonFormat(pattern = "yyyy-MM-dd")
  val dateOfBirth: LocalDate? = null,
  val gender: Gender? = null,
  val location: Location? = null,
  val sexuallyMotivatedOffenceHistory: Boolean? = null,
) {
  companion object {
    private fun sexuallyMotivatedOffenceHistoryFrom(value: String?): Boolean? {
      return when (value?.lowercase()) {
        "yes" -> true
        "no" -> false
        null -> null
        else -> throw IllegalArgumentException("Invalid value for sexually motivated offence history: $value")
      }
    }

    fun from(request: SubjectDetailsRequest): SubjectDetails {
      return SubjectDetails(
        request.crn,
        request.pnc,
        request.nomisId,
        request.givenName,
        request.familyName,
        request.dateOfBirth,
        Gender.from(request.gender),
        request.location,
        sexuallyMotivatedOffenceHistoryFrom(request.sexuallyMotivatedOffenceHistory),
      )
    }
  }
}
