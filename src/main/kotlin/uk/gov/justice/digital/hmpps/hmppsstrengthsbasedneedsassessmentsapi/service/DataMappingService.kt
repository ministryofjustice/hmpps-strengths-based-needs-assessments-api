package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.MappingProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.FormVersionNotFoundException

@Service
class DataMappingService(
  val formConfigProvider: FormConfigProvider,
  val mappingProvider: MappingProvider,
) {
  fun getOasysEquivalent(assessment: AssessmentVersion): OasysEquivalent {
    val formVersion = assessment.assessment?.info?.formVersion
      ?: throw FormVersionNotFoundException("No form version found for assessment ID ${assessment.id}")
    val formConfig = formConfigProvider.get(formVersion)
    val answersProvider = AnswersProvider(assessment.answers, formConfig)

    val mapping = mappingProvider.get(formVersion)

    return mapping.fold(emptyMap()) { acc, sectionMapping -> acc + sectionMapping.map(answersProvider) }
  }
}
