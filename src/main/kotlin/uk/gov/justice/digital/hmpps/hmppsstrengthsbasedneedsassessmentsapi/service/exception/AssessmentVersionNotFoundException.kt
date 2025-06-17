package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

import jakarta.persistence.EntityNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import java.util.UUID

class AssessmentVersionNotFoundException : EntityNotFoundException {
  constructor(criteria: AssessmentVersionCriteria) :
    super("No assessment version found that matches criteria: $criteria")

  constructor(versionUuid: UUID) :
    super("No assessment version found with provided UUID: $versionUuid")
}
