package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class OneTimeLinkException :
  ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid one time link")
