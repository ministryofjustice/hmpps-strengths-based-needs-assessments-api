package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria

import org.springframework.data.jpa.domain.Specification
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment_
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

data class AssessmentVersionCriteria(
  val assessmentUuid: UUID,
  val tags: Set<Tag>? = null,
  val after: Long? = null,
  val until: Long? = null,
) {
  fun getSpecification(): Specification<AssessmentVersion> {
    return belongsToAssessment()
      .and(hasTag())
      .and(isAfter())
      .and(isBefore())
  }

  private fun belongsToAssessment(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      builder.equal(root.get(AssessmentVersion_.assessment).get(Assessment_.uuid), assessmentUuid)
    }
  }

  private fun hasTag(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      if (!tags.isNullOrEmpty()) {
        builder.and(root.get(AssessmentVersion_.tag).`in`(tags))
      } else {
        null
      }
    }
  }

  private fun isAfter(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      if (after != null) {
        val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(after), ZoneId.systemDefault())
        builder.greaterThan(root.get(AssessmentVersion_.updatedAt), date)
      } else {
        null
      }
    }
  }

  private fun isBefore(): Specification<AssessmentVersion> {
    return Specification<AssessmentVersion> { root, _, builder ->
      if (until != null) {
        val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(until), ZoneId.systemDefault())
        builder.lessThan(root.get(AssessmentVersion_.updatedAt), date)
      } else {
        null
      }
    }
  }
}
