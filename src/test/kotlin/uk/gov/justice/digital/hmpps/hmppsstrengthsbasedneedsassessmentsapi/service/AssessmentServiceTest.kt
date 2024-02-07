package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentService")
class AssessmentServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val assessmentService = AssessmentService(
    assessmentRepository = assessmentRepository,
  )

  @Nested
  @DisplayName("createAssessment")
  inner class CreateAssessment {
    @Test
    fun `it creates and returns an assessment`() {
      val assessmentUUID = UUID.randomUUID()
      val assessment = Assessment(id = 1, uuid = assessmentUUID)

      every { assessmentRepository.save(any()) } returns assessment

      val result = assessmentService.createAssessment()
      assertThat(result).isEqualTo(assessment)
    }
  }

  @Nested
  @DisplayName("findByUuid")
  inner class FindByUuid {
    @Test
    fun `it returns the assessment when one exists for the given UUID`() {
      val assessmentUUID = UUID.randomUUID()
      val assessment = Assessment(id = 1, uuid = assessmentUUID)

      every { assessmentRepository.findByUuid(assessmentUUID) } returns assessment

      val result = assessmentService.findByUuid(assessmentUUID)
      assertThat(result).isEqualTo(assessment)
    }

    @Test
    fun `it throws when no assessment exists for the given UUID`() {
      val assessmentUUID = UUID.randomUUID()

      every { assessmentRepository.findByUuid(assessmentUUID) } returns null

      assertThrows<AssessmentNotFoundException> { assessmentService.findByUuid(assessmentUUID) }
    }
  }
}
