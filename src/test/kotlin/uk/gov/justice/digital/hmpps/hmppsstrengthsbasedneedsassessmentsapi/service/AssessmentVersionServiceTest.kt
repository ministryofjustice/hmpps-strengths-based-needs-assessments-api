package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UserDetails
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersionAudit
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.OasysEquivalent
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.SignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionAuditRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentVersionService")
class AssessmentVersionServiceTest {
  private val assessmentVersionRepository: AssessmentVersionRepository = mockk()
  private val assessmentVersionAuditRepository: AssessmentVersionAuditRepository = mockk()
  private val dataMappingService: DataMappingService = mockk()
  private val assessmentVersionService = AssessmentVersionService(
    assessmentVersionRepository = assessmentVersionRepository,
    dataMappingService = dataMappingService,
    assessmentVersionAuditRepository = assessmentVersionAuditRepository,
  )

  private val tag = Tag.UNSIGNED

  private val firstAssessmentVersion = AssessmentVersion(
    tag = tag,
    updatedAt = LocalDateTime.now().minusDays(1).minusHours(1),
    answers = mapOf("foo" to Answer(AnswerType.TEXT, "Foo question", null, "Foo answer", null)),
    versionNumber = 0,
  )

  private val secondAssessmentVersion = AssessmentVersion(
    tag = tag,
    updatedAt = LocalDateTime.now().minusHours(1),
    answers = mapOf("test" to Answer(value = "val")),
    versionNumber = 1,
  )

  @BeforeEach
  fun setUp() {
    clearAllMocks()
  }

  @Nested
  @DisplayName("clonePreviousOrCreateNew")
  inner class GetPreviousOrCreate {
    @Test
    fun `it returns the previous assessment version if it was created today`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> =
        PageImpl(listOf(secondAssessmentVersion, firstAssessmentVersion))

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      val result = assessmentVersionService.getPreviousOrCreate(assessment)
      assertThat(result?.tag).isEqualTo(tag)
      assertThat(result?.uuid).isEqualTo(secondAssessmentVersion.uuid)
      assertThat(result?.answers?.get("test")?.value).isEqualTo("val")
      assertThat(result?.versionNumber).isEqualTo(1)
    }

    @Test
    fun `it clones from a previous assessment version if it wasn't created today`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> =
        PageImpl(listOf(firstAssessmentVersion))

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      every {
        assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid)
      } returns assessmentVersions.totalElements.toInt()

      val result = assessmentVersionService.getPreviousOrCreate(assessment)
      assertThat(result?.uuid).isNotEqualTo(firstAssessmentVersion.uuid)
      assertThat(result?.tag).isEqualTo(tag)
      assertThat(result?.answers?.get("foo")?.value).isEqualTo("Foo answer")
      assertThat(result?.versionNumber).isEqualTo(1)
    }

    @Test
    fun `it returns a new version when there is no previous one to clone from`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> = Page.empty()

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      every {
        assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid)
      } returns assessmentVersions.totalElements.toInt()

      val result = assessmentVersionService.getPreviousOrCreate(assessment)
      assertThat(result?.tag).isEqualTo(tag)
      assertThat(result?.answers).isEmpty()
      assertThat(result?.versionNumber).isEqualTo(0)
    }
  }

  @Nested
  @DisplayName("find")
  inner class Find {
    @Test
    fun `it finds the latest assessment version for a given tag`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())

      val assessmentVersions: Page<AssessmentVersion> =
        PageImpl(listOf(firstAssessmentVersion, secondAssessmentVersion))

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      val specification = AssessmentVersionCriteria(assessmentUuid = assessment.uuid, tags = setOf(tag))
      val result = assessmentVersionService.findOrNull(specification)

      assertThat(result).isEqualTo(firstAssessmentVersion)
    }

    @Test
    fun `it returns null when there is no assessment version for a given tag`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> = PageImpl(emptyList())

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      val specification = AssessmentVersionCriteria(assessmentUuid = assessment.uuid, tags = setOf(tag))
      val result = assessmentVersionService.findOrNull(specification)

      assertThat(result).isNull()
    }
  }

  @Nested
  @DisplayName("setOasysEquivalent")
  inner class SetOasysEquivalent {
    private val assessmentVersion = AssessmentVersion()
    private val oasysEquivalent: OasysEquivalent = mapOf("foo" to "bar")

    @Test
    fun `it sets OASys equivalent data`() {
      every { dataMappingService.getOasysEquivalent(match { it.uuid == assessmentVersion.uuid }) } returns oasysEquivalent

      assessmentVersionService.setOasysEquivalents(assessmentVersion)

      assertThat(assessmentVersion.oasysEquivalents).isEqualTo(oasysEquivalent)

      verify(exactly = 1) { dataMappingService.getOasysEquivalent(any()) }
    }
  }

  @Nested
  @DisplayName("updateAnswers")
  inner class UpdateAnswers {
    private val assessment = Assessment(id = 1, uuid = UUID.randomUUID())

    private val assessmentVersions: Page<AssessmentVersion> = PageImpl(
      listOf(
        AssessmentVersion(
          tag = tag,
          updatedAt = LocalDateTime.of(2023, 12, 1, 12, 0),
          answers = mapOf(
            "foo" to Answer(AnswerType.TEXT, "Foo question", null, "not updated", null),
            "bar" to Answer(AnswerType.TEXT, "Bar question", null, "not updated", null),
            "baz" to Answer(AnswerType.TEXT, "Baz question", null, "should be removed", null),
          ),
        ),
        AssessmentVersion(
          tag = tag,
          updatedAt = LocalDateTime.of(2023, 6, 1, 12, 0),
          answers = emptyMap(),
        ),
      ),
    )

    @Test
    fun `it updates answers for a given assessment`() {
      val request = UpdateAssessmentAnswersRequest(
        answersToAdd = mapOf("foo" to Answer(AnswerType.TEXT, "Foo question", null, "updated", null)),
        answersToRemove = listOf("baz"),
      )

      val oasysEquivalents = mapOf("foo" to "bar")

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions
      every {
        assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid)
      } returns assessmentVersions.totalElements.toInt()
      every { dataMappingService.getOasysEquivalent(any()) } returns oasysEquivalents

      val savedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(savedVersion)) } returnsArgument 0

      assessmentVersionService.updateAnswers(assessment, request)

      assertThat(savedVersion.captured.assessment).isEqualTo(assessment)
      assertThat(savedVersion.captured.tag).isEqualTo(tag)
      assertThat(savedVersion.captured.answers["foo"]?.value).isEqualTo("updated")
      assertThat(savedVersion.captured.answers["bar"]?.value).isEqualTo("not updated")
      assertThat(savedVersion.captured.answers["baz"]).isNull()
      assertThat(savedVersion.captured.oasysEquivalents).isEqualTo(oasysEquivalents)
      assertThat(savedVersion.captured.updatedAt).isAfter(assessmentVersions.maxOf { it.updatedAt })
    }
  }

  @Nested
  @DisplayName("lock")
  inner class Lock {
    private val assessment = Assessment()

    @Test
    fun `it locks an assessment successfully`() {
      val assessmentVersion = AssessmentVersion(assessment = assessment, tag = Tag.UNSIGNED)
      val user = UserDetails("user-id", "User Name")

      val lockedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(lockedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      val result = assessmentVersionService.lock(assessmentVersion, user)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }

      assertThat(result).isEqualTo(lockedVersion.captured)
      assertThat(result.tag).isEqualTo(Tag.LOCKED_INCOMPLETE)
      assertThat(assessmentVersion.tag).isEqualTo(Tag.LOCKED_INCOMPLETE)

      assertThat(audit.captured.assessmentVersion).isEqualTo(lockedVersion.captured)
      assertThat(audit.captured.statusFrom).isEqualTo(Tag.UNSIGNED)
      assertThat(audit.captured.statusTo).isEqualTo(Tag.LOCKED_INCOMPLETE)
      assertThat(audit.captured.userDetails).isEqualTo(user)
    }

    @Test
    fun `it throws an exception when the assessment is already locked`() {
      val lockedVersion = AssessmentVersion(
        assessment = assessment,
        tag = Tag.LOCKED_INCOMPLETE,
      )

      val exception = assertThrows<ConflictException> { assessmentVersionService.lock(lockedVersion, UserDetails()) }
      assertThat(exception.message).isEqualTo("The current assessment version is already locked")

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }
  }

  @Nested
  @DisplayName("sign")
  inner class Sign {
    private val assessment = Assessment()
    private val assessmentVersion = AssessmentVersion(assessment = assessment)

    @ParameterizedTest
    @EnumSource(SignType::class)
    fun `it signs an assessment successfully`(signType: SignType) {
      val unsignedVersion = AssessmentVersion(
        assessment = assessment,
        tag = Tag.UNSIGNED,
        answers = mapOf(Field.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
      )

      val expectedTag: Tag = signType.into()
      val signer = UserDetails("signer-id", "Signer Name")

      val signedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(signedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      val result = assessmentVersionService.sign(unsignedVersion, signer, signType)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }

      assertThat(result).isEqualTo(signedVersion.captured)
      assertThat(result.tag).isEqualTo(expectedTag)
      assertThat(unsignedVersion.tag).isEqualTo(expectedTag)

      assertThat(audit.captured.assessmentVersion).isEqualTo(signedVersion.captured)
      assertThat(audit.captured.statusFrom).isEqualTo(Tag.UNSIGNED)
      assertThat(audit.captured.statusTo).isEqualTo(expectedTag)
      assertThat(audit.captured.userDetails).isEqualTo(signer)
    }

    @ParameterizedTest
    @EnumSource(SignType::class)
    fun `it throws an exception when the assessment is already signed`(signType: SignType) {
      val signedVersion = AssessmentVersion(
        assessment = assessment,
        tag = signType.into(),
        answers = mapOf(Field.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
      )

      val exception = assertThrows<ConflictException> {
        assessmentVersionService.sign(signedVersion, UserDetails(), signType)
      }
      assertEquals("The current assessment version is already ${signedVersion.tag.name}.", exception.message)

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }

    @Test
    fun `it throws an exception when the assessment is not completed`() {
      val exception = assertThrows<ConflictException> {
        assessmentVersionService.sign(assessmentVersion, UserDetails(), SignType.SELF)
      }
      assertEquals("The current assessment version is not completed.", exception.message)

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }
  }

  @Nested
  @DisplayName("counter-sign")
  inner class CounterSign {
    private val assessment = Assessment()
    private val assessmentVersion = AssessmentVersion(assessment = assessment)

    private fun validCounterSignStatusesProvider(): List<Arguments> {
      return setOf(
        Tag.COUNTERSIGNED,
        Tag.AWAITING_DOUBLE_COUNTERSIGN,
        Tag.DOUBLE_COUNTERSIGNED,
        Tag.REJECTED,
      ).flatMap { outcome ->
        setOf(Tag.AWAITING_COUNTERSIGN, Tag.AWAITING_DOUBLE_COUNTERSIGN).map { initialStatus ->
          Arguments.of(initialStatus, outcome)
        }
      }
    }

    @ParameterizedTest
    @MethodSource("validCounterSignStatusesProvider")
    fun `it counter-signs an assessment successfully`(initialStatus: Tag, outcome: Tag) {
      val counterSigner = UserDetails("signer-id", "Signer Name")

      val counterSignedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(counterSignedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      assessmentVersion.tag = initialStatus

      val result = assessmentVersionService.counterSign(assessmentVersion, counterSigner, outcome)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }

      assertThat(result).isEqualTo(counterSignedVersion.captured)
      assertThat(result.tag).isEqualTo(outcome)
      assertThat(assessmentVersion.tag).isEqualTo(outcome)

      assertThat(audit.captured.assessmentVersion).isEqualTo(counterSignedVersion.captured)
      assertThat(audit.captured.statusFrom).isEqualTo(initialStatus)
      assertThat(audit.captured.statusTo).isEqualTo(outcome)
      assertThat(audit.captured.userDetails).isEqualTo(counterSigner)
    }

    @ParameterizedTest
    @EnumSource(
      Tag::class,
      mode = EnumSource.Mode.EXCLUDE,
      names = ["COUNTERSIGNED", "AWAITING_DOUBLE_COUNTERSIGN", "DOUBLE_COUNTERSIGNED", "REJECTED"],
    )
    fun `it throws an exception when the requested outcome status is invalid`(outcome: Tag) {
      val exception = assertThrows<ConflictException> {
        assessmentVersionService.counterSign(assessmentVersion, UserDetails(), outcome)
      }
      assertEquals("Invalid outcome status ${outcome.name}.", exception.message)

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }

    @ParameterizedTest
    @EnumSource(
      Tag::class,
      mode = EnumSource.Mode.EXCLUDE,
      names = ["AWAITING_COUNTERSIGN", "AWAITING_DOUBLE_COUNTERSIGN"],
    )
    fun `it throws an exception when the initial assessment status is unexpected`(initialStatus: Tag) {
      assessmentVersion.tag = initialStatus

      val exception = assertThrows<ConflictException> {
        assessmentVersionService.counterSign(assessmentVersion, UserDetails(), Tag.COUNTERSIGNED)
      }
      assertEquals(
        "Cannot counter-sign this assessment version. Unexpected status ${initialStatus.name}.",
        exception.message,
      )

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }
  }

  @Nested
  @DisplayName("rollback")
  inner class Rollback {
    private val assessment = Assessment()
    private val assessmentVersion = AssessmentVersion(assessment = assessment)

    @ParameterizedTest
    @EnumSource(
      Tag::class,
      mode = EnumSource.Mode.INCLUDE,
      names = [
        "AWAITING_COUNTERSIGN",
        "AWAITING_DOUBLE_COUNTERSIGN",
        "COUNTERSIGNED",
        "DOUBLE_COUNTERSIGNED",
        "LOCKED_INCOMPLETE",
        "REJECTED",
        "SELF_SIGNED",
      ],
    )
    fun `it does a rollback on the assessment successfully`(tag: Tag) {
      val userDetails = UserDetails("signer-id", "Signer Name")

      val rolledBackVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(rolledBackVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      assessmentVersion.tag = tag

      val result = assessmentVersionService.rollback(assessmentVersion, userDetails)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }

      assertThat(result).isEqualTo(rolledBackVersion.captured)
      assertThat(result.tag).isEqualTo(Tag.ROLLED_BACK)

      assertThat(audit.captured.assessmentVersion).isEqualTo(rolledBackVersion.captured)
      assertThat(audit.captured.statusFrom).isEqualTo(tag)
      assertThat(audit.captured.statusTo).isEqualTo(Tag.ROLLED_BACK)
      assertThat(audit.captured.userDetails).isEqualTo(userDetails)
    }

    @ParameterizedTest
    @EnumSource(
      Tag::class,
      mode = EnumSource.Mode.EXCLUDE,
      names = [
        "AWAITING_COUNTERSIGN",
        "AWAITING_DOUBLE_COUNTERSIGN",
        "COUNTERSIGNED",
        "DOUBLE_COUNTERSIGNED",
        "LOCKED_INCOMPLETE",
        "REJECTED",
        "SELF_SIGNED",
      ],
    )
    fun `it throws when the requested version status is invalid`(tag: Tag) {
      val userDetails = UserDetails("signer-id", "Signer Name")

      val rolledBackVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(rolledBackVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      assessmentVersion.tag = tag

      val exception =
        assertThrows<ConflictException> { assessmentVersionService.rollback(assessmentVersion, userDetails) }

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }

      assertThat(exception.message).isEqualTo("Cannot rollback this assessment version. Unexpected status ${tag.name}.")
    }
  }
}
