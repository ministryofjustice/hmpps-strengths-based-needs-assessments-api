package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

import jakarta.persistence.EntityNotFoundException

class SubjectNotFoundException(message: String) : EntityNotFoundException(message)
