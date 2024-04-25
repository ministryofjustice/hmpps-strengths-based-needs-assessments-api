package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

import jakarta.persistence.EntityNotFoundException

class OasysAssessmentNotFoundException(oasysAssessmentPk: String) :
  EntityNotFoundException("No OASys assessment found for PK $oasysAssessmentPk")
