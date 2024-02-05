package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

import jakarta.persistence.EntityNotFoundException

class FormVersionNotFoundException(message: String) : EntityNotFoundException(message)
