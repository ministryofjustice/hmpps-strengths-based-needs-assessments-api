package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Session

data class OneTimeLinkResponse(
  val link: String,
) {
  companion object {
    private const val baseUrl = "https://foo.bar"

    fun from(session: Session) = with(session) { OneTimeLinkResponse("$baseUrl/session/$linkUuid") }
  }
}
