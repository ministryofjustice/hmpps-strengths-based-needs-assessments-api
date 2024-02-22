package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.SectionMapping
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.exception.MappingNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.v1.Accommodation
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo

@Component
class MappingProvider {
  fun get(formInfo: AssessmentFormInfo): Set<SectionMapping> {
    return versions[formInfo.formVersion] ?: throw MappingNotFoundException(formInfo.formVersion)
  }

  companion object {
    private val versions = mapOf(
      "1.0" to setOf(
        Accommodation(),
      ),
    )
  }
}
