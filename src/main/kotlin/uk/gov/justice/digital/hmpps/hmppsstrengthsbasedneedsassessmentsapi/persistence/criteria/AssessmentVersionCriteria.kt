package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria

import org.springframework.data.jpa.domain.Specification
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.util.UUID

data class AssessmentVersionCriteria(
  val assessmentUuid: UUID,
  val tags: Set<Tag>? = null,
  val versionNumber: Long? = null,
) {
  fun getSpecification(): Specification<AssessmentVersion> {
    return belongsToAssessment()
      .and(hasTag())
      .and(isVersionNumber())
  }

  private fun belongsToAssessment(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      builder.equal(root.get(AssessmentVersion_.assessment).get(Assessment_.uuid), assessmentUuid)
    }
  }

  private fun hasTag(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      tags?.takeIf { it.isNotEmpty() }?.let { builder.and(root.get(AssessmentVersion_.tag).`in`(tags)) }
    }
  }

  private fun isVersionNumber(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      versionNumber?.let {
        builder.equal(root.get(AssessmentVersion_.versionNumber), it)
      }
    }
  }
}
