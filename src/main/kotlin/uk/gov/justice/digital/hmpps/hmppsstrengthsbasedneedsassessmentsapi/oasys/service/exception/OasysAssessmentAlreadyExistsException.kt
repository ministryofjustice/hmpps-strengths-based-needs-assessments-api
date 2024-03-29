package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException

class OasysAssessmentAlreadyExistsException(oasysAssessmentPk: String) : ConflictException("OASys assessment with ID $oasysAssessmentPk already exists")
