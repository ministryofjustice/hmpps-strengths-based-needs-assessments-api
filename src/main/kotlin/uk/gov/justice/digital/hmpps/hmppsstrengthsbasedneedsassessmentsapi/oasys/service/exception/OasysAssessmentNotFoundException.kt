package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException

class OasysAssessmentNotFoundException(oasysAssessmentPk: String) :
  ConflictException("No previous OASys assessment found for PK $oasysAssessmentPk")
