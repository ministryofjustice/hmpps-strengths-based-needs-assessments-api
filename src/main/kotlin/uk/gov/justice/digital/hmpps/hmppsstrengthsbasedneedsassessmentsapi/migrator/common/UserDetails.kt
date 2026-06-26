package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common

enum class AuthSource {
  OASYS,
  HMPPS_AUTH,
  NOT_SPECIFIED,
}

data class UserDetails(
  val id: String,
  val name: String,
  val authSource: AuthSource = AuthSource.NOT_SPECIFIED,
) {
  companion object {
    fun from(practitioner: uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails?): UserDetails =
      UserDetails(practitioner?.id ?: "UNKNOWN", practitioner?.name ?: "Unknown", AuthSource.OASYS)

    fun fromUsername(username: String): UserDetails = UserDetails("UNKNOWN", username, AuthSource.OASYS)
  }
}
