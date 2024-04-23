package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

class OasysAssessmentNotFoundException(oasysAssessmentPk: String) :
  RuntimeException("No previous OASys assessment found for PK $oasysAssessmentPk")
