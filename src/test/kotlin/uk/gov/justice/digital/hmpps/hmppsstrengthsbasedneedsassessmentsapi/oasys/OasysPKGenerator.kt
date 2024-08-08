package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys

class OasysPKGenerator {
  companion object {
    fun new(): String {
      val allowedChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
      return List(15) { allowedChars.random() }.joinToString("")
    }
  }
}
