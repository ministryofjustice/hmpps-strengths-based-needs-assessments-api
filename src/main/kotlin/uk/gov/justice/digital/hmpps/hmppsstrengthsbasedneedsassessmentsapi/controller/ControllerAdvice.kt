package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response.ErrorResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.UserNotAuthenticatedException

@RestControllerAdvice
class ControllerAdvice {
  @ExceptionHandler(UserNotAuthenticatedException::class)
  fun handle(ex: UserNotAuthenticatedException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(
        ErrorResponse(
          userMessage = "User not authorized",
          developerMessage = ex.message ?: "",
        ),
      )
  }

  @ExceptionHandler(EntityNotFoundException::class)
  fun handler(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(
        ErrorResponse(
          userMessage = "Not found",
          developerMessage = ex.message ?: "",
        ),
      )
  }

  @ExceptionHandler(ConflictException::class)
  fun handler(ex: ConflictException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(HttpStatus.CONFLICT)
      .body(
        ErrorResponse(
          userMessage = ex.message ?: "",
          developerMessage = ex.message ?: "",
        ),
      )
  }

  @ExceptionHandler(ResponseStatusException::class)
  fun handler(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(ex.statusCode)
      .body(
        ErrorResponse(
          userMessage = ex.reason ?: "",
          developerMessage = ex.message ?: "",
        ),
      )
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
  fun handler(ex: org.springframework.security.access.AccessDeniedException): ResponseEntity<ErrorResponse> {
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(
        ErrorResponse(
          userMessage = "Access denied",
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
