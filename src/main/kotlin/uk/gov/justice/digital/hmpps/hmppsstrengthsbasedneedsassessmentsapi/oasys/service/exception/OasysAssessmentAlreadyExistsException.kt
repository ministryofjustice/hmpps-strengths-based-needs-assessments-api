package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

class OasysAssessmentAlreadyExistsException(oasysAssessmentPk: String) : RuntimeException("OASys assessment with ID $oasysAssessmentPk already exists")
