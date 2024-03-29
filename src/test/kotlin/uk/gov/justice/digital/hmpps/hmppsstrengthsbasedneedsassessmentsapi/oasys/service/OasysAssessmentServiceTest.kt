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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyLockedException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException

@ExtendWith(MockKExtension::class)
@DisplayName("OasysAssessmentService")
class OasysAssessmentServiceTest {
  private val assessmentService: AssessmentService = mockk()
  private val assessmentVersionService: AssessmentVersionService = mockk()
  private val oasysAssessmentRepository: OasysAssessmentRepository = mockk()
  private val oasysAssessmentService = OasysAssessmentService(
    assessmentService,
    assessmentVersionService,
    oasysAssessmentRepository,
  )
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
      every { assessmentService.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.createAssessmentWithOasysId(oasysAssessmentPk)

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      verify { assessmentService.save(any()) }
    }
  }

  @Nested
  @DisplayName("findOrCreateAssessment")
  inner class FindOrCreateAssessment {
    @Test
    fun `it finds an assessment for a given OASys assessment PK`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPk(any()) } returns OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      val result = oasysAssessmentService.findOrCreateAssessment(oasysAssessmentPk)

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)
    }

    @Test
    fun `it creates an assessment if unable to find one for a given OASys assessment PK`() {
      every { assessmentService.save(any()) } returnsArgument 0
      every { oasysAssessmentRepository.findByOasysAssessmentPk(any()) } returns null

      val result = oasysAssessmentService.findOrCreateAssessment(oasysAssessmentPk)

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      verify { assessmentService.save(any()) }
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

    @Test
    fun `it throws when unable to find an assessment for a given OASys assessment PK`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPk(any()) } returns null

      assertThrows<OasysAssessmentNotFoundException> { oasysAssessmentService.find(oasysAssessmentPk) }
    }
  }

  @Nested
  @DisplayName("associate")
  inner class Associate {
    @Test
    fun `it throws when an association already exists for the given OASys assessment PK`() {
      val request = AssociateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
        oldOasysAssessmentPk = "0987654321",
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk) } returns OasysAssessment(
        oasysAssessmentPk = request.oasysAssessmentPk,
        assessment = assessment,
      )

      assertThrows<OasysAssessmentAlreadyExistsException> { oasysAssessmentService.associate(request) }
    }

    @Test
    fun `it creates an assessment when no old OASys assessment PK is provided`() {
      val request = AssociateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
      )

      val assessment = Assessment(
        assessmentVersions = listOf(assessmentVersion),
        oasysAssessments = listOf(OasysAssessment(oasysAssessmentPk = oasysAssessmentPk)),
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk) } throws OasysAssessmentNotFoundException("Not found")
      every { assessmentService.save(any()) } returns assessment
      every { assessmentVersionService.find(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associate(request)

      assertThat(result).isEqualTo(assessmentVersion)
    }

    @Test
    fun `it associates an assessment when an old OASys assessment PK is provided`() {
      val request = AssociateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
        oldOasysAssessmentPk = "0987654321",
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk) } throws OasysAssessmentNotFoundException("Not found")
      every { oasysAssessmentRepository.findByOasysAssessmentPk(request.oldOasysAssessmentPk!!) } returns OasysAssessment(
        oasysAssessmentPk = request.oldOasysAssessmentPk!!,
        assessment = assessment,
      )
      val association = slot<OasysAssessment>()
      every { oasysAssessmentRepository.save(capture(association)) } returnsArgument 0
      every { assessmentVersionService.find(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associate(request)

      assertThat(association.captured.oasysAssessmentPk).isEqualTo(request.oasysAssessmentPk)
      assertThat(association.captured.assessment).isEqualTo(assessment)
      assertThat(result).isEqualTo(assessmentVersion)
    }
  }

  @Nested
  @DisplayName("lock")
  inner class Lock {
    @Test
    fun `it clones and locks an assessment successfully`() {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )
      val lockedVersion = AssessmentVersion()

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns assessmentVersion
      every { assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED) } returns lockedVersion

      val result = oasysAssessmentService.lock(oasysAssessmentPk)

      verify(exactly = 1) { assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED) }

      assertThat(result).isEqualTo(lockedVersion)
    }

    @Test
    fun `it throws an exception when the assessment is already locked`() {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )
      val lockedVersion = AssessmentVersion(
        assessment = assessment,
        tag = Tag.LOCKED,
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns lockedVersion
      every { assessmentVersionService.cloneAndTag(any(), any()) } throws RuntimeException()

      assertThrows<OasysAssessmentAlreadyLockedException> { oasysAssessmentService.lock(oasysAssessmentPk) }

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }

    @Test
    fun `it throws an exception when no assessment version is found`() {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } throws AssessmentVersionNotFoundException("test")

      assertThrows<AssessmentVersionNotFoundException> { oasysAssessmentService.lock(oasysAssessmentPk) }

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }

    @Test
    fun `it throws an exception when no OASys assessment is found`() {
      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } throws OasysAssessmentNotFoundException("test")

      assertThrows<OasysAssessmentNotFoundException> { oasysAssessmentService.lock(oasysAssessmentPk) }

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }
  }
}
