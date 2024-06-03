package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request

class TransferAssociationRequest(
  val oldOasysAssessmentPK: String,
  val newOasysAssessmentPK: String,
)
