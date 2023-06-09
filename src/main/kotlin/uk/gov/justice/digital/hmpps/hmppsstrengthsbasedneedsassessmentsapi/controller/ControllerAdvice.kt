package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.dto.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OneTimeLinkException

@RestControllerAdvice
class ControllerAdvice {
  @ExceptionHandler(OneTimeLinkException::class)
  fun handle(ex: OneTimeLinkException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        ErrorResponse(
          userMessage = "Unable to use one time link",
          developerMessage = ex.message ?: "",
        ),
      )
  }

  @ExceptionHandler(Exception::class)
  fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
    log.error("Exception: ", ex)
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ErrorResponse())
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
