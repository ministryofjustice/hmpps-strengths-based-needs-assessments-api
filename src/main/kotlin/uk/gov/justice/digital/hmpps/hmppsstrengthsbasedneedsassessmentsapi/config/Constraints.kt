package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.config

class Constraints {
  companion object {
    const val OASYS_PK_MAX_LENGTH = 15
    const val OASYS_PK_MIN_LENGTH = 1
    const val REGION_PRISON_CODE_MAX_LENGTH = 15
    const val OASYS_USER_ID_MAX_LENGTH = 15
    const val OASYS_USER_NAME_MAX_LENGTH = 64
  }
}
