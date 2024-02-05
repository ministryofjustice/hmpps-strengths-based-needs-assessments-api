package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

import jakarta.persistence.EntityNotFoundException

class AssessmentNotFoundException(message: String) : EntityNotFoundException(message)
