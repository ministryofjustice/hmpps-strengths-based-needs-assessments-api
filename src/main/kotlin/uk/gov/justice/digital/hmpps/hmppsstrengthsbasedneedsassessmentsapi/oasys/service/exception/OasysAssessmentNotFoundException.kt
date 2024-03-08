package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

import jakarta.persistence.EntityNotFoundException

class OasysAssessmentNotFoundException(message: String) : EntityNotFoundException(message)
