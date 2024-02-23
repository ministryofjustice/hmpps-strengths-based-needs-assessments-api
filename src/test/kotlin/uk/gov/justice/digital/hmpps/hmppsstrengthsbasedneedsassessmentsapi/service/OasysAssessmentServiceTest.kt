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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.AssociateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.OasysAssessmentNotFoundException

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
}
