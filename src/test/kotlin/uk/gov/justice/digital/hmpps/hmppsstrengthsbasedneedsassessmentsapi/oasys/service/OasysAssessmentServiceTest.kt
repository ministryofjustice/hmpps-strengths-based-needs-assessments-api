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
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CounterSignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CreateAssessmentRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.util.UUID
import kotlin.test.assertEquals

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
      val assessment = Assessment()
      every { assessmentService.createAssessment() } returns assessment
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.createAssessmentWithOasysId(oasysAssessmentPk)

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)

      verify(exactly = 1) { assessmentService.createAssessment() }
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
      val assessment = Assessment()

      every { assessmentService.createAssessment() } returns assessment
      every { oasysAssessmentRepository.findByOasysAssessmentPk(any()) } returns null
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0

      val result = oasysAssessmentService.findOrCreateAssessment(oasysAssessmentPk)

      assertThat(result.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result.assessment).isEqualTo(assessment)

      verify(exactly = 1) { assessmentService.createAssessment() }
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
      assertThat(result?.oasysAssessmentPk).isEqualTo(oasysAssessmentPk)
      assertThat(result?.assessment).isEqualTo(assessment)
    }
  }

  @Nested
  @DisplayName("associate")
  inner class Associate {
    @Test
    fun `it throws when an association already exists for the given OASys assessment PK`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
        previousOasysAssessmentPk = "0987654321",
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk) } returns OasysAssessment(
        oasysAssessmentPk = request.oasysAssessmentPk,
        assessment = assessment,
      )

      assertThrows<OasysAssessmentAlreadyExistsException> {
        oasysAssessmentService.associate(request.oasysAssessmentPk, request.previousOasysAssessmentPk)
      }
    }

    @Test
    fun `it creates an assessment when no old OASys assessment PK is provided`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
      )

      val assessmentUuid = UUID.randomUUID()

      val assessment = Assessment(
        uuid = assessmentUuid,
        assessmentVersions = listOf(assessmentVersion),
        oasysAssessments = listOf(
          OasysAssessment(oasysAssessmentPk = oasysAssessmentPk, assessment = Assessment(uuid = assessmentUuid)),
        ),
      )

      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk)
      } returns null
      every { oasysAssessmentRepository.save(any()) } returnsArgument 0
      every { assessmentService.createAssessment() } returns assessment
      every { assessmentVersionService.find(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associate(request.oasysAssessmentPk)

      verify(exactly = 1) { assessmentService.createAssessment() }

      assertThat(result.uuid).isEqualTo(assessment.uuid)
    }

    @Test
    fun `it associates an assessment when an old OASys assessment PK is provided`() {
      val request = CreateAssessmentRequest(
        oasysAssessmentPk = "1234567890",
        previousOasysAssessmentPk = "0987654321",
      )

      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(request.oasysAssessmentPk)
      } returns null
      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(request.previousOasysAssessmentPk!!)
      } returns OasysAssessment(
        oasysAssessmentPk = request.previousOasysAssessmentPk!!,
        assessment = assessment,
      )
      val association = slot<OasysAssessment>()
      every { oasysAssessmentRepository.save(capture(association)) } returnsArgument 0
      every { assessmentVersionService.find(any()) } returns assessmentVersion

      val result = oasysAssessmentService.associate(request.oasysAssessmentPk, request.previousOasysAssessmentPk)

      assertThat(association.captured.oasysAssessmentPk).isEqualTo(request.oasysAssessmentPk)
      assertThat(association.captured.assessment).isEqualTo(assessment)
      assertThat(result.uuid).isEqualTo(assessment.uuid)
    }
  }

  @Nested
  @DisplayName("sign")
  inner class Sign {
    private val unsignedVersion = AssessmentVersion(
      assessment = assessment,
      tag = Tag.UNSIGNED,
      answers = mapOf(Field.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
    )

    private fun getExpectedTag(counterSignType: CounterSignType?): Tag {
      return when (counterSignType) {
        CounterSignType.SELF -> Tag.SELF_SIGNED
        CounterSignType.COUNTERSIGN -> Tag.AWAITING_COUNTERSIGN
        else -> fail("Unhandled countersign type")
      }
    }

    @ParameterizedTest
    @EnumSource(CounterSignType::class)
    fun `it clones and signs an assessment successfully`(counterSignType: CounterSignType) {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )
      val signedVersion = AssessmentVersion()
      val expectedTag = getExpectedTag(counterSignType)

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns unsignedVersion
      every { assessmentVersionService.cloneAndTag(unsignedVersion, expectedTag) } returns signedVersion

      val result = oasysAssessmentService.sign(oasysAssessmentPk, counterSignType)

      verify(exactly = 1) { assessmentVersionService.cloneAndTag(unsignedVersion, expectedTag) }

      assertThat(result).isEqualTo(signedVersion)
    }

    @ParameterizedTest
    @EnumSource(CounterSignType::class)
    fun `it throws an exception when the assessment is already signed`(counterSignType: CounterSignType) {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )
      val signedVersion = AssessmentVersion(
        assessment = assessment,
        tag = getExpectedTag(counterSignType),
        answers = mapOf(Field.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns signedVersion
      every { assessmentVersionService.cloneAndTag(any(), any()) } throws RuntimeException()

      val exception = assertThrows<ConflictException> { oasysAssessmentService.sign(oasysAssessmentPk, counterSignType) }
      assertEquals("The current assessment version is already ${signedVersion.tag.name}.", exception.message)

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }

    @Test
    fun `it throws an exception when the assessment is not completed`() {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = assessment,
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns assessmentVersion
      every { assessmentVersionService.cloneAndTag(any(), any()) } throws RuntimeException()

      val exception = assertThrows<ConflictException> { oasysAssessmentService.sign(oasysAssessmentPk, CounterSignType.SELF) }
      assertEquals("The current assessment version is not completed.", exception.message)

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }

    @Test
    fun `it throws an exception when no OASys assessment is found`() {
      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
      } throws OasysAssessmentNotFoundException("1234567890")

      assertThrows<OasysAssessmentNotFoundException> { oasysAssessmentService.sign(oasysAssessmentPk, CounterSignType.SELF) }

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
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
      every { assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED_INCOMPLETE) } returns lockedVersion

      val result = oasysAssessmentService.lock(oasysAssessmentPk)

      verify(exactly = 1) { assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED_INCOMPLETE) }

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
        tag = Tag.LOCKED_INCOMPLETE,
      )

      every { oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) } returns oasysAssessment
      every { assessmentVersionService.find(match { it.assessmentUuid == assessment.uuid }) } returns lockedVersion
      every { assessmentVersionService.cloneAndTag(any(), any()) } throws RuntimeException()

      val exception = assertThrows<ConflictException> { oasysAssessmentService.lock(oasysAssessmentPk) }
      assertThat(exception.message).isEqualTo("OASys assessment with ID $oasysAssessmentPk has already been locked")

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }

    @Test
    fun `it throws an exception when no OASys assessment is found`() {
      every {
        oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
      } throws OasysAssessmentNotFoundException("1234567890")

      assertThrows<OasysAssessmentNotFoundException> { oasysAssessmentService.lock(oasysAssessmentPk) }

      verify(exactly = 0) { assessmentVersionService.cloneAndTag(any(), any()) }
    }
  }
}
