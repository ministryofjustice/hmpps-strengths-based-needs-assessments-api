package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception

class OasysAssessmentAlreadyExistsException(oasysAssessmentPk: String) : RuntimeException("OASys assessment with ID $oasysAssessmentPk already exists")
