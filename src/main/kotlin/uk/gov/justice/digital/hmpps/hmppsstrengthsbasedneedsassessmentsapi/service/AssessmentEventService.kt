package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentAggregate
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentEvent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.EventData
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.User
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentAggregateRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentEventRepository
import java.time.Clock
import java.time.LocalDateTime

@Service
class AssessmentEventService(
  private val eventRepository: AssessmentEventRepository,
  private val aggregateRepository: AssessmentAggregateRepository,
  private val clock: Clock = Clock.systemDefaultZone(),
) {
  private fun now() = LocalDateTime.now(clock)

  private fun getLatestAggregateFor(assessment: Assessment) =
    aggregateRepository.findFirstByAssessmentUuidOrderByToDesc(assessment.uuid)

  @Transactional
  fun save(assessment: Assessment, user: User, eventData: EventData) {
    val latestEvent = AssessmentEvent.from(assessment, user, eventData)

    eventRepository.save(latestEvent)

    val latestAggregate = getLatestAggregateFor(assessment)
      ?: createAggregate( assessment, assessment.createdAt, now())

    latestAggregate.apply {
      applyEvents(latestEvent)
      updatedAt = now()
    }

    aggregateRepository.save(latestAggregate)
  }

  fun find(assessment: Assessment): List<AssessmentEvent> = eventRepository.findAllByAssessmentUuid(assessment.uuid)

  fun getAssessment(assessment: Assessment): AssessmentAggregate {
    return createAggregate(assessment, assessment.createdAt, now())
      .apply {
        val events = eventRepository.findAllByAssessmentUuid(assessment.uuid)
        applyEvents(events)
      }
  }

  fun getChangesBetween(assessment: Assessment, start: LocalDateTime, end: LocalDateTime): AssessmentAggregate {
    return createAggregate(assessment, start, end)
      .apply {
        val events = eventRepository.findAllByAssessmentUuidAndCreatedAtBetween(assessment.uuid, start, end)
        applyEvents(events)
      }
  }

  private fun createAggregate(assessment: Assessment, from: LocalDateTime, to: LocalDateTime) =
    AssessmentAggregate(assessment = assessment, from = from, to = to)
}
