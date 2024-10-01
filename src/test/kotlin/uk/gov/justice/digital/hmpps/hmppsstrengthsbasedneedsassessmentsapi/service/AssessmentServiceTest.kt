package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentNotFoundException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentService")
class AssessmentServiceTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val formConfigProvider: FormConfigProvider = mockk()
  private val assessmentVersionService: AssessmentVersionService = mockk()
  private val assessmentVersionAuditRepository: AssessmentVersionAuditRepository = mockk()
  private val assessmentService = AssessmentService(
    formConfigProvider = formConfigProvider,
    assessmentRepository = assessmentRepository,
    assessmentVersionService = assessmentVersionService,
    assessmentVersionAuditRepository = assessmentVersionAuditRepository,
  )

  @Nested
  @DisplayName("create")
  inner class Create {
    @Test
    fun `it creates and returns an assessment`() {
      val formConfig = FormConfig("version")
      val assessmentSlot = slot<Assessment>()

      every { assessmentRepository.save(capture(assessmentSlot)) } returnsArgument 0
      every { formConfigProvider.getLatest() } returns formConfig
      every { assessmentVersionService.setOasysEquivalents(any()) } returnsArgument 0

      val result = assessmentService.create()

      verify(exactly = 1) { formConfigProvider.getLatest() }
      verify(exactly = 1) { assessmentRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionService.setOasysEquivalents(any()) }

      assertThat(result.uuid).isEqualTo(assessmentSlot.captured.uuid)
      assertThat(result.info?.formVersion).isEqualTo(formConfig.version)
    }

    @Test
    fun `it creates and returns an assessment with audit`() {
      val formConfig = FormConfig("version")
      val assessmentSlot = slot<Assessment>()
      val assessmentVersionAudit = slot<AssessmentVersionAudit>()
      val userDetails = UserDetails("1", "TestUser")

      every { assessmentRepository.save(capture(assessmentSlot)) } returnsArgument 0
      every { formConfigProvider.getLatest() } returns formConfig
      every { assessmentVersionService.setOasysEquivalents(any()) } returnsArgument 0
      every { assessmentVersionAuditRepository.save(capture(assessmentVersionAudit)) } returnsArgument 0

      val result = assessmentService.createAndAudit(userDetails)

      assertThat(result.uuid).isEqualTo(assessmentSlot.captured.uuid)
      assertThat(assessmentVersionAudit.captured.userDetails).isEqualTo(userDetails)
      assertThat(result.info?.formVersion).isEqualTo(formConfig.version)
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
