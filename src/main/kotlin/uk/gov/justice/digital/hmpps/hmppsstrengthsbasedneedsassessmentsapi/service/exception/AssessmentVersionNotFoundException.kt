package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

import jakarta.persistence.EntityNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria

class AssessmentVersionNotFoundException(criteria: AssessmentVersionCriteria) :
  EntityNotFoundException("No assessment version found that matches criteria: $criteria")
