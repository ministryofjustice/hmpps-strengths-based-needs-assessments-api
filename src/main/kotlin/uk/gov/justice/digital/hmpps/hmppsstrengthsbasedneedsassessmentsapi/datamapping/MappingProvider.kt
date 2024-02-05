package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception.MappingNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.v1.Accommodation

class MappingProvider {
  fun get(version: String?): Set<SectionMapping> {
    return versions[version] ?: throw MappingNotFoundException(version ?: "unknown")
  }

  companion object {
    private val versions = mapOf(
      "1.0" to setOf(
        Accommodation(),
      ),
    )
  }
}
