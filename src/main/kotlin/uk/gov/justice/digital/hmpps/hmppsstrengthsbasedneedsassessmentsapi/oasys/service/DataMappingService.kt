package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.MappingProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.common.AnswersProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.FormVersionNotFoundException

@Service
class DataMappingService(
  val formConfigProvider: FormConfigProvider,
  val mappingProvider: MappingProvider,
) {
  fun getOasysEquivalent(assessmentVersion: AssessmentVersion): OasysEquivalent {
    val formInfo = assessmentVersion.assessment.info
      ?: throw FormVersionNotFoundException("No form version found for assessment ID ${assessmentVersion.id}")
    val formConfig = formConfigProvider.get(formInfo)
    val answersProvider = AnswersProvider(assessmentVersion.answers, formConfig)

    val mapping = mappingProvider.get(formInfo)

    return mapping.fold(emptyMap()) { acc, sectionMapping -> acc + sectionMapping.map(answersProvider) }
  }
}
