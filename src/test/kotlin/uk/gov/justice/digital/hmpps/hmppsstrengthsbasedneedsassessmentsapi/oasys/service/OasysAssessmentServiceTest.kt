package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("OasysAssessmentService")
class OasysAssessmentServiceTest {
  private val assessmentService: AssessmentService = mockk()
  private val assessmentVersionService: AssessmentVersionService = mockk()
  private val oasysAssessmentRepository: OasysAssessmentRepository = mockk()
  private val oasysAssessmentService = OasysAssessmentService(assessmentService, oasysAssessmentRepository)
  private val oasysAssessmentPk = "1234567890"
  private val assessment = Assessment()
  private val assessmentVersion = AssessmentVersion(assessment = assessment)

  @BeforeEach
  fun setUp() {
    clearAllMocks()
  }

  @Nested
  @DisplayName("createAssessmentWithOasysId")
  inner class CreateAssessmentWithOasysId {
    @Test
    fun `it creates an assessment for a given OASys assessment PK`() {
      val assessment = Assessment()
      every { assessmentService.create() } returns assessment
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.createAssessmentWithOasysId(oasysAssessmentPk, "test-prison-code")

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)
      assertThat(result.regionPrisonCode).isEqualTo("test-prison-code")

      verify(exactly = 1) { assessmentService.create() }
    }
  }

  @Nested
  @DisplayName("find")
  inner class Find {
    @Test
    fun `it finds an assessment for a given OASys assessment PK`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(any()) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      val result = oasysAssessmentService.find(oasysAssessmentPk)
      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)
    }

    @Test
    fun `it throws an exception when an OASys assessment is not found`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(any()) } returns null

      val exception = assertThrows<EntityNotFoundException> { oasysAssessmentService.find(oasysAssessmentPk) }
      assertThat(exception.message).isEqualTo("No OASys assessment found for PK $oasysAssessmentPk")
    }

    @Test
    fun `it throws an exception when an OASys assessment is soft deleted`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(any()) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
        deleted = true,
      )

      val exception = assertThrows<EntityNotFoundException> { oasysAssessmentService.find(oasysAssessmentPk) }
      assertThat(exception.message).isEqualTo("OASys assessment PK $oasysAssessmentPk is soft deleted")
    }
  }

  @Nested
  @DisplayName("associateExistingOrCreate")
  inner class AssociateExistingOrCreate {
    @Test
    fun `it throws when an association already exists for the given OASys assessment PK`() {
      val oasysAssessmentPk = "1234567890"
      val previousOasysAssessmentPk = "0987654321"

      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(oasysAssessmentPk) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      val exception = assertThrows<ConflictException> {
        oasysAssessmentService.associateExistingOrCreate(oasysAssessmentPk, previousOasysAssessmentPk)
      }
      assertThat(exception.message).isEqualTo("OASys assessment with ID $oasysAssessmentPk already exists.")
    }

    @Test
    fun `it throws when an association already exists for the given OASys assessment PK and is soft deleted`() {
      val oasysAssessmentPk = "1234567890"
      val previousOasysAssessmentPk = "0987654321"

      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(oasysAssessmentPk) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
        deleted = true,
      )

      val exception = assertThrows<ConflictException> {
        oasysAssessmentService.associateExistingOrCreate(oasysAssessmentPk, previousOasysAssessmentPk)
      }
      assertThat(exception.message).isEqualTo("OASys assessment with ID $oasysAssessmentPk is soft deleted.")
    }

    @Test
    fun `it creates an assessment when no old OASys assessment PK is provided`() {
      val oasysAssessmentPk = "1234567890"
      val assessmentUuid = UUID.randomUUID()

      val assessment = Assessment(
        uuid = assessmentUuid,
        assessmentVersions = listOf(assessmentVersion),
        oasysAssessments = listOf(
          OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = Assessment(uuid = assessmentUuid)),
        ),
      )

      every {
        oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(oasysAssessmentPk)
      } returns null
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0
      every { assessmentService.create() } returns assessment
      every { assessmentVersionService.findOrNull(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associateExistingOrCreate(oasysAssessmentPk)

      verify(exactly = 1) { assessmentService.create() }

      assertThat(result.uuid).isEqualTo(assessment.uuid)
    }

    @Test
    fun `it associates an assessment when an old OASys assessment PK is provided`() {
      val oasysAssessmentPk = "1234567890"
      val previousOasysAssessmentPk = "0987654321"
      val regionPrisonCode = "test-prison-code"

      every {
        oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(oasysAssessmentPk)
      } returns null
      every {
        oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(previousOasysAssessmentPk)
      } returns OasysAssessment(
        oasysAssessmentPk = previousOasysAssessmentPk,
        assessment = assessment,
      )
      val association = slot<OasysAssessment>()
      every { oasysAssessmentRepository.save(capture(association)) } returnsArgument 0
      every { assessmentVersionService.findOrNull(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associateExistingOrCreate(oasysAssessmentPk, previousOasysAssessmentPk, regionPrisonCode)

      assertThat(association.captured.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(association.captured.assessment).isEqualTo(assessment)
      assertThat(association.captured.regionPrisonCode).isEqualTo(regionPrisonCode)
      assertThat(result.uuid).isEqualTo(assessment.uuid)
    }
  }

  @Nested
  @DisplayName("softDelete")
  inner class SoftDelete {
    @Test
    fun `it soft-deletes an assessment for a given OASys assessment PK`() {
      val assessment = OasysAssessment()

      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.softDelete(assessment)

      assertThat(result.uuid).isEqualTo(assessment.uuid)
      assertThat(result.deleted).isTrue

      verify(exactly = 1) { oasysAssessmentRepository.save(any()) }
    }

    @Test
    fun `it throws a Conflict when the assessment is already deleted`() {
      val assessment = OasysAssessment(deleted = true, oasysAssessmentPk = "1234")

      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val exception = assertThrows<ConflictException> { oasysAssessmentService.softDelete(assessment) }
      assertThat(exception.message).isEqualTo("OASys assessment 1234 has already been soft-deleted.")

      assertThat(assessment.deleted).isTrue

      verify(exactly = 0) { oasysAssessmentRepository.save(any()) }
    }
  }

  @Nested
  @DisplayName("undelete")
  inner class Undelete {
    @Test
    fun `it undeletes an assessment for a given OASys assessment PK`() {
      val assessment = OasysAssessment(deleted = true, oasysAssessmentPk = "1234")

      every { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(match { it == assessment.oasysAssessmentPk }) } returns assessment
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.undelete(assessment.oasysAssessmentPk)

      assertThat(result.uuid).isEqualTo(assessment.uuid)
      assertThat(result.deleted).isFalse

      verify(exactly = 1) { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(any()) }
      verify(exactly = 1) { oasysAssessmentRepository.save(any()) }
    }

    @Test
    fun `it throws a Conflict when the assessment is not deleted`() {
      val assessment = OasysAssessment(oasysAssessmentPk = "1234")

      every { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(match { it == assessment.oasysAssessmentPk }) } returns null
      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(match { it == assessment.oasysAssessmentPk }) } returns assessment

      val exception = assertThrows<ConflictException> { oasysAssessmentService.undelete(assessment.oasysAssessmentPk) }
      assertThat(exception.message)
        .isEqualTo("Cannot undelete OASys assessment PK ${assessment.oasysAssessmentPk} because it is not deleted.")

      assertThat(assessment.deleted).isFalse

      verify(exactly = 1) { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(any()) }
      verify(exactly = 1) { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(any()) }
      verify(exactly = 0) { oasysAssessmentRepository.save(any()) }
    }

    @Test
    fun `it throws a Not Found when the assessment is not found`() {
      val oasysAssessmentPk = "1234"

      every { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(match { it == oasysAssessmentPk }) } returns null
      every { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(match { it == oasysAssessmentPk }) } returns null

      val exception = assertThrows<EntityNotFoundException> { oasysAssessmentService.undelete(oasysAssessmentPk) }
      assertThat(exception.message).isEqualTo("No OASys assessment found for PK $oasysAssessmentPk")

      verify(exactly = 1) { oasysAssessmentRepository.findDeletedByOasysAssessmentPk(any()) }
      verify(exactly = 1) { oasysAssessmentRepository.findByOasysAssessmentPkInclDeleted(any()) }
      verify(exactly = 0) { oasysAssessmentRepository.save(any()) }
    }
  }
}
