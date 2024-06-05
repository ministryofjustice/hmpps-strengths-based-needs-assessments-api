package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
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
      every { oasysAssessmentRepository.findByOasysAssessmentPk(any()) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      val result = oasysAssessmentService.find(oasysAssessmentPk)
      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)
    }
  }

  @Nested
  @DisplayName("associate")
  inner class Associate {
    @Test
    fun `it throws when an association already exists for the given OASys assessment PK`() {
      val oasysAssessmentPk = "1234567890"
      val previousOasysAssessmentPk = "0987654321"

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      assertThrows<OasysAssessmentAlreadyExistsException> {
        oasysAssessmentService.associateExistingOrCreate(oasysAssessmentPk, previousOasysAssessmentPk)
      }
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
        oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
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
        oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
      } returns null
      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(previousOasysAssessmentPk)
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
}
