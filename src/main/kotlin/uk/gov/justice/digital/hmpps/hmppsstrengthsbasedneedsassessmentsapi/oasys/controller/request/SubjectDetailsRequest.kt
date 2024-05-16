package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Location
import java.time.LocalDate

data class SubjectDetailsRequest(
  val crn: String?,
  val pnc: String?,
  val nomisId: String?,
  val givenName: String,
  val familyName: String,
  val dateOfBirth: LocalDate?,
  val gender: Int,
  val location: Location,
  val sexuallyMotivatedOffenceHistory: String?,
)
