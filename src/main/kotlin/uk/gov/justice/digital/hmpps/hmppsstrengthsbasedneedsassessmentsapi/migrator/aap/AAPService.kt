package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.CreateTimelineItemCommand
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.Requestable
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.Timeline
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.request.CommandsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.aap.commands.request.CommandsResponse
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.AuthSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.migrator.common.UserDetails
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse
import java.time.LocalDateTime
import java.util.UUID

@Service
class AAPService(
  @param:Qualifier("assessmentPlatformClient")
  private val assessmentPlatformClient: WebClient,
) {
  fun primeAuthToken() {
    try {
      assessmentPlatformClient
        .post()
        .uri { uriBuilder -> uriBuilder.path("/command").queryParam("backdateTo", LocalDateTime.now().toString()).build() }
        .bodyValue(
          CommandsRequest(
            listOf(
              CreateTimelineItemCommand(
                LocalDateTime.now(),
                UserDetails(
                  id = "some-id",
                  name = "name",
                  authSource = AuthSource.OASYS,
                ),
                Timeline(
                  type = "AUTH_TOKEN_PRIMED",
                  data = mapOf(),
                ),
                assessmentUuid = "some-assessment-uuid",
              ),
            ),
          ),
        )
        .retrieve()
        .bodyToMono(CommandsResponse::class.java)
        .block()
    } catch (e: Exception) {}
  }
  fun dispatchCommands(timestamp: LocalDateTime, commands: List<Requestable>): CommandsResponse = assessmentPlatformClient
    .post()
    .uri { uriBuilder -> uriBuilder.path("/command").queryParam("backdateTo", timestamp.toString()).build() }
    .bodyValue(CommandsRequest(commands))
    .retrieve()
    .onStatus(
      { it.is5xxServerError },
      {
        it.bodyToMono(ErrorResponse::class.java)
          .map { res -> IllegalStateException("Call to Assessment Platform API failed with status ${res.status}: ${res.developerMessage}") }
      },
    )
    .bodyToMono(CommandsResponse::class.java)
    .block()
    ?: throw RuntimeException("Empty response from Assessment Platform API")

  fun deleteAssessment(assessmentUuid: UUID) = assessmentPlatformClient
    .delete()
    .uri("/assessment/$assessmentUuid")
    .retrieve()
    .toBodilessEntity()
    .block()
}
