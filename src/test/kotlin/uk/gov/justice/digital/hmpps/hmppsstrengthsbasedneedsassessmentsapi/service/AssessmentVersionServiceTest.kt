package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfigProvider
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.DataMappingService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentFormInfo
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
import kotlin.test.assertTrue
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field as MappingField

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentVersionService")
class AssessmentVersionServiceTest {
  private val assessmentVersionRepository: AssessmentVersionRepository = mockk()
  private val assessmentVersionAuditRepository: AssessmentVersionAuditRepository = mockk()
  private val dataMappingService: DataMappingService = mockk()
  private val telemetryService: TelemetryService = mockk()
  private val formConfigProvider: FormConfigProvider = mockk()
  private val assessmentVersionService = AssessmentVersionService(
    assessmentVersionRepository = assessmentVersionRepository,
    assessmentVersionAuditRepository = assessmentVersionAuditRepository,
    dataMappingService = dataMappingService,
    telemetryService = telemetryService,
    formConfigProvider = formConfigProvider,
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
    every { formConfigProvider.getLatest() } returns FormConfig("1.0")
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
    private val assessment = Assessment(info = AssessmentFormInfo(formVersion = "1.0"))
    private val oasysEquivalents = mapOf("foo" to "bar")

    private val request = UpdateAssessmentAnswersRequest(
      answersToAdd = mapOf("foo" to Answer(AnswerType.TEXT, "Foo question", null, "updated", null)),
      answersToRemove = listOf("baz"),
      userDetails = UserDetails(),
    )

    private val testConfig = FormConfig(
      "1.0",
      mapOf(
        "foo" to Field(code = "foo", section = "foo"),
        "foo_2" to Field(code = "foo_2", section = "foo"),
        "bar" to Field(code = "bar", section = "bar"),
        "baz" to Field(code = "baz", section = "baz"),
        "qux" to Field(code = "qux", section = "qux"),
        "foo_section_complete" to Field(code = "foo_section_complete", section = "foo"),
        "bar_section_complete" to Field(code = "bar_section_complete", section = "bar"),
        "baz_section_complete" to Field(code = "baz_section_complete", section = "baz"),
        "qux_section_complete" to Field(code = "qux_section_complete", section = "qux"),
      ),
    )

    @BeforeEach
    fun setUp() {
      every { dataMappingService.getOasysEquivalent(any()) } returns oasysEquivalents
      every { assessmentVersionRepository.countVersionWhereAssessmentUuid(assessment.uuid) } returns 1
      every { formConfigProvider.get(assessment.info!!) } returns testConfig
      every { telemetryService.assessmentAnswersUpdated(any(), any(), any()) } just Runs
      every { telemetryService.assessmentCompleted(any(), any()) } just Runs
      every { telemetryService.sectionCompleted(any(), any(), any()) } just Runs
      every { telemetryService.sectionUpdated(any(), any(), any(), any()) } just Runs
      every { telemetryService.questionUpdated(any(), any(), any(), any(), any(), any()) } just Runs
    }

    @Test
    fun `it updates answers for a given assessment version`() {
      val latestVersion = AssessmentVersion(
        tag = tag,
        assessment = assessment,
        answers = mapOf(
          "foo" to Answer(AnswerType.TEXT, "Foo question", null, "not updated", null),
          "bar" to Answer(AnswerType.TEXT, "Bar question", null, "not updated", null),
          "baz" to Answer(AnswerType.TEXT, "Baz question", null, "should be removed", null),
          "qux_section_complete" to Answer(AnswerType.TEXT, "Section complete", null, "YES", null),
        ),
        versionNumber = 0,
      )

      val savedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(savedVersion)) } returnsArgument 0

      val request = UpdateAssessmentAnswersRequest(
        answersToAdd = mapOf(
          "foo" to Answer(AnswerType.TEXT, "Foo question", null, "updated", null),
          "foo_2" to Answer(AnswerType.TEXT, "Foo 2 question", null, "added", null),
          "foo_section_complete" to Answer(AnswerType.TEXT, "Section complete", null, "YES", null),
          "bar_section_complete" to Answer(AnswerType.TEXT, "Section complete", null, "YES", null),
          "baz_section_complete" to Answer(AnswerType.TEXT, "Section complete", null, "NO", null),
          "qux_section_complete" to Answer(AnswerType.TEXT, "Section complete", null, "YES", null),
          "assessment_complete" to Answer(AnswerType.TEXT, "Assessment complete", null, "YES", null),
        ),
        answersToRemove = listOf("baz"),
        userDetails = UserDetails("user-id"),
      )

      assessmentVersionService.updateAnswers(latestVersion, request)

      assertThat(savedVersion.captured.assessment).isEqualTo(assessment)
      assertThat(savedVersion.captured.tag).isEqualTo(tag)
      assertThat(savedVersion.captured.answers["foo"]?.value).isEqualTo("updated")
      assertThat(savedVersion.captured.answers["foo_2"]?.value).isEqualTo("added")
      assertThat(savedVersion.captured.answers["bar"]?.value).isEqualTo("not updated")
      assertThat(savedVersion.captured.answers["baz"]).isNull()
      assertThat(savedVersion.captured.answers["foo_section_complete"]?.value).isEqualTo("YES")
      assertThat(savedVersion.captured.answers["bar_section_complete"]?.value).isEqualTo("YES")
      assertThat(savedVersion.captured.answers["baz_section_complete"]?.value).isEqualTo("NO")
      assertThat(savedVersion.captured.answers["qux_section_complete"]?.value).isEqualTo("YES")
      assertThat(savedVersion.captured.answers["assessment_complete"]?.value).isEqualTo("YES")
      assertThat(savedVersion.captured.oasysEquivalents).isEqualTo(oasysEquivalents)
      assertThat(savedVersion.captured.versionNumber).isEqualTo(0)

      verify(exactly = 1) { telemetryService.assessmentAnswersUpdated(any(), any(), any()) }
      verify(exactly = 1) { telemetryService.assessmentCompleted(any(), any()) }
      verify(exactly = 2) { telemetryService.sectionCompleted(any(), any(), any()) }
      verify(exactly = 3) { telemetryService.sectionUpdated(any(), any(), any(), any()) }
      verify(exactly = 7) { telemetryService.questionUpdated(any(), any(), any(), any(), any(), any()) }

      verify(exactly = 1) {
        telemetryService.assessmentAnswersUpdated(
          withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
          request.userDetails.id,
          Tag.UNSIGNED,
        )
      }

      verify(exactly = 1) {
        telemetryService.assessmentCompleted(
          withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
          request.userDetails.id,
        )
      }

      listOf("foo", "bar").forEach {
        verify(exactly = 1) {
          telemetryService.sectionCompleted(
            withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
            request.userDetails.id,
            it,
          )
        }
      }

      listOf("foo", "bar", "baz").forEach {
        verify(exactly = 1) {
          telemetryService.sectionUpdated(
            withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
            request.userDetails.id,
            Tag.UNSIGNED,
            it,
          )
        }
      }

      mapOf(
        "foo" to "foo",
        "foo_2" to "foo",
        "foo_section_complete" to "foo",
        "bar_section_complete" to "bar",
        "baz_section_complete" to "baz",
        "assessment_complete" to "Unknown",
      ).forEach { (code, section) ->
        verify(exactly = 1) {
          telemetryService.questionUpdated(
            withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
            request.userDetails.id,
            Tag.UNSIGNED,
            section,
            code,
            false,
          )
        }
      }

      verify(exactly = 1) {
        telemetryService.questionUpdated(
          withArg { assertEquals(savedVersion.captured.uuid, it.uuid) },
          request.userDetails.id,
          Tag.UNSIGNED,
          "baz",
          "baz",
          true,
        )
      }
    }

    @Test
    fun `it clones from a previous assessment version if it wasn't created today`() {
      val latestVersion = AssessmentVersion(
        tag = tag,
        assessment = assessment,
        updatedAt = LocalDateTime.of(2023, 12, 1, 12, 0),
        answers = mapOf(
          "foo" to Answer(AnswerType.TEXT, "Foo question", null, "not updated", null),
          "bar" to Answer(AnswerType.TEXT, "Bar question", null, "not updated", null),
          "baz" to Answer(AnswerType.TEXT, "Baz question", null, "should be removed", null),
        ),
      )

      val savedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(savedVersion)) } returnsArgument 0

      assessmentVersionService.updateAnswers(latestVersion, request)

      assertThat(savedVersion.captured.assessment).isEqualTo(assessment)
      assertThat(savedVersion.captured.tag).isEqualTo(tag)
      assertThat(savedVersion.captured.answers["foo"]?.value).isEqualTo("updated")
      assertThat(savedVersion.captured.answers["bar"]?.value).isEqualTo("not updated")
      assertThat(savedVersion.captured.answers["baz"]).isNull()
      assertThat(savedVersion.captured.oasysEquivalents).isEqualTo(oasysEquivalents)
      assertThat(savedVersion.captured.versionNumber).isEqualTo(1)
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
      every { telemetryService.assessmentStatusUpdated(any(), any(), any()) } just Runs

      val result = assessmentVersionService.lock(assessmentVersion, user)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }
      verify(exactly = 1) { telemetryService.assessmentStatusUpdated(assessmentVersion, user.id, Tag.UNSIGNED) }

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
        answers = mapOf(MappingField.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
      )

      val expectedTag: Tag = signType.into()
      val signer = UserDetails("signer-id", "Signer Name")

      val signedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(signedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0
      every { telemetryService.assessmentStatusUpdated(any(), any(), any()) } just Runs

      val result = assessmentVersionService.sign(unsignedVersion, signer, signType)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }
      verify(exactly = 1) { telemetryService.assessmentStatusUpdated(withArg { assertEquals(signedVersion.captured.uuid, it.uuid) }, signer.id, Tag.UNSIGNED) }

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
        answers = mapOf(MappingField.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
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
      every { telemetryService.assessmentStatusUpdated(any(), any(), any()) } just Runs

      assessmentVersion.tag = initialStatus

      val result = assessmentVersionService.counterSign(assessmentVersion, counterSigner, outcome)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }
      verify(exactly = 1) { telemetryService.assessmentStatusUpdated(assessmentVersion, counterSigner.id, initialStatus) }

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
      every { telemetryService.assessmentStatusUpdated(any(), any(), any()) } just Runs

      assessmentVersion.tag = tag

      val result = assessmentVersionService.rollback(assessmentVersion, userDetails)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }
      verify(exactly = 1) { telemetryService.assessmentStatusUpdated(assessmentVersion, userDetails.id, tag) }

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

  @Nested
  @DisplayName("softDelete")
  inner class SoftDelete {
    @BeforeEach
    fun setUp() {
      every { assessmentVersionRepository.saveAll(any<List<AssessmentVersion>>()) } returnsArgument 0
      every { telemetryService.assessmentSoftDeleted(any(), any(), any()) } just Runs
    }

    @Test
    fun `it soft-deletes the provided assessment versions`() {
      val user = UserDetails("user-id", "User Name")
      val assessment = Assessment()
      assessment.assessmentVersions = listOf(
        AssessmentVersion(assessment = assessment),
        AssessmentVersion(assessment = assessment),
      )

      val deletedVersions = assessmentVersionService.softDelete(assessment.assessmentVersions, user)

      assertThat(deletedVersions.count()).isEqualTo(2)
      assertTrue(deletedVersions.all { version -> version.deleted })

      verify(exactly = 1) { assessmentVersionRepository.saveAll(any<List<AssessmentVersion>>()) }
      verify(exactly = 1) { telemetryService.assessmentSoftDeleted(withArg { assertEquals(assessment.uuid, it.uuid) }, user.id, withArg { assertEquals(it.count(), 2) }) }
    }

    @Test
    fun `it throws a Conflict when the assessment versions are already deleted`() {
      val assessmentVersions = listOf(
        AssessmentVersion(deleted = true),
        AssessmentVersion(deleted = true),
      )

      val exception = assertThrows<ConflictException> {
        assessmentVersionService.softDelete(assessmentVersions, UserDetails("user-id", "User Name"))
      }
      assertThat(exception.message).isEqualTo("No assessment versions found for deletion")

      verify(exactly = 0) { assessmentVersionRepository.saveAll(any<List<AssessmentVersion>>()) }
      verify(exactly = 0) { telemetryService.assessmentSoftDeleted(any(), any(), any()) }
    }
  }

  @Nested
  @DisplayName("undelete")
  inner class Undelete {
    @BeforeEach
    fun setUp() {
      every { assessmentVersionRepository.saveAll(any<List<AssessmentVersion>>()) } returnsArgument 0
      every { telemetryService.assessmentUndeleted(any(), any(), any()) } just Runs
    }

    @Test
    fun `it undeletes the requested assessment versions`() {
      val user = UserDetails("user-id", "User Name")
      val assessment = Assessment()
      val assessmentVersions = listOf(
        AssessmentVersion(deleted = true, versionNumber = 0, assessment = assessment),
        AssessmentVersion(deleted = true, versionNumber = 1, assessment = assessment),
        AssessmentVersion(deleted = true, versionNumber = 2, assessment = assessment),
        AssessmentVersion(deleted = true, versionNumber = 3, assessment = assessment),
      )

      every { assessmentVersionRepository.findAllDeleted(match { it == assessment.uuid }) } returns assessmentVersions

      val undeletedVersions = assessmentVersionService.undelete(assessment, 1, 3, user)

      assertThat(undeletedVersions.count()).isEqualTo(2)
      assertTrue(undeletedVersions.all { version -> with(version) { !deleted && versionNumber in listOf(1, 2) } })

      verify(exactly = 1) { assessmentVersionRepository.findAllDeleted(match { it == assessment.uuid }) }
      verify(exactly = 1) {
        assessmentVersionRepository.saveAll<AssessmentVersion>(
          match { versions ->
            versions.count() == 2 &&
              versions.all { version -> with(version) { !deleted && versionNumber in listOf(1, 2) } }
          },
        )
      }
      verify(exactly = 1) { telemetryService.assessmentUndeleted(assessment, user.id, undeletedVersions) }
    }

    @Test
    fun `it throws a Conflict when the assessment is not deleted`() {
      val assessment = Assessment()

      every { assessmentVersionRepository.findAllDeleted(match { it == assessment.uuid }) } returns emptyList()

      val exception = assertThrows<ConflictException> {
        assessmentVersionService.undelete(assessment, 0, null, UserDetails("user-id", "User Name"))
      }
      assertThat(exception.message)
        .isEqualTo("No assessment versions found for un-deletion")

      verify(exactly = 1) { assessmentVersionRepository.findAllDeleted(match { it == assessment.uuid }) }
      verify(exactly = 0) { assessmentVersionRepository.saveAll(any<List<AssessmentVersion>>()) }
      verify(exactly = 0) { telemetryService.assessmentUndeleted(any(), any(), any()) }
    }
  }
}
