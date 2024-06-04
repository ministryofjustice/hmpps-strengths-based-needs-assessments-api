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
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CounterSignType
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
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.UserDetails
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

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
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
      } returns assessmentVersions.totalElements

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
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
      } returns assessmentVersions.totalElements

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
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

      assessmentVersionService.setOasysEquivalent(assessmentVersion)

      assertThat(assessmentVersion.oasys_equivalent).isEqualTo(oasysEquivalent)

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
        tags = setOf(tag),
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
      } returns assessmentVersions.totalElements
      every { dataMappingService.getOasysEquivalent(any()) } returns oasysEquivalents

      val savedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(savedVersion)) } returnsArgument 0

      assessmentVersionService.updateAnswers(assessment, request)

      assertThat(savedVersion.captured.assessment).isEqualTo(assessment)
      assertThat(savedVersion.captured.tag).isEqualTo(tag)
      assertThat(savedVersion.captured.answers["foo"]?.value).isEqualTo("updated")
      assertThat(savedVersion.captured.answers["bar"]?.value).isEqualTo("not updated")
      assertThat(savedVersion.captured.answers["baz"]).isNull()
      assertThat(savedVersion.captured.oasys_equivalent).isEqualTo(oasysEquivalents)
      assertThat(savedVersion.captured.updatedAt).isAfter(assessmentVersions.maxOf { it.updatedAt })
    }

    @Test
    fun `it throws an exception when attempting to update a locked assessment version`() {
      val request = UpdateAssessmentAnswersRequest(
        tags = setOf(Tag.LOCKED_INCOMPLETE),
        answersToAdd = mapOf("foo" to Answer(AnswerType.TEXT, "Foo question", null, "updated", null)),
        answersToRemove = listOf("baz"),
      )

      assertThrows<ConflictException> { assessmentVersionService.updateAnswers(assessment, request) }

      verify(exactly = 0) { assessmentVersionRepository.findAll(any<Specification<AssessmentVersion>>(), any<PageRequest>()) }
      verify(exactly = 0) { dataMappingService.getOasysEquivalent(any()) }
      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
    }
  }

  @Nested
  @DisplayName("lock")
  inner class Lock {
    private val assessment = Assessment()
    private val assessmentVersion = AssessmentVersion(assessment = assessment)

    @Test
    fun `it locks an assessment successfully`() {
      val lockedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(lockedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      val result = assessmentVersionService.lock(assessmentVersion)

      verify(exactly = 1) { assessmentVersionRepository.save(any()) }
      verify(exactly = 1) { assessmentVersionAuditRepository.save(any()) }

      assertThat(result).isEqualTo(lockedVersion.captured)
      assertThat(result.tag).isEqualTo(Tag.LOCKED_INCOMPLETE)
      assertThat(assessmentVersion.tag).isEqualTo(Tag.LOCKED_INCOMPLETE)

      assertThat(audit.captured.assessmentVersion).isEqualTo(lockedVersion.captured)
      assertThat(audit.captured.statusFrom).isEqualTo(Tag.UNSIGNED)
      assertThat(audit.captured.statusTo).isEqualTo(Tag.LOCKED_INCOMPLETE)
      assertThat(audit.captured.userDetails).isEqualTo(UserDetails())
    }

    @Test
    fun `it throws an exception when the assessment is already locked`() {
      val lockedVersion = AssessmentVersion(
        assessment = assessment,
        tag = Tag.LOCKED_INCOMPLETE,
      )

      val exception = assertThrows<ConflictException> { assessmentVersionService.lock(lockedVersion) }
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
    fun `it signs an assessment successfully`(counterSignType: CounterSignType) {
      val expectedTag = getExpectedTag(counterSignType)
      val signer = UserDetails("signer-id", "Signer Name")

      val signedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(signedVersion)) } returnsArgument 0

      val audit = slot<AssessmentVersionAudit>()
      every { assessmentVersionAuditRepository.save(capture(audit)) } returnsArgument 0

      val result = assessmentVersionService.sign(unsignedVersion, counterSignType, signer)

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
    @EnumSource(CounterSignType::class)
    fun `it throws an exception when the assessment is already signed`(counterSignType: CounterSignType) {
      val signedVersion = AssessmentVersion(
        assessment = assessment,
        tag = getExpectedTag(counterSignType),
        answers = mapOf(Field.ASSESSMENT_COMPLETE.lower to Answer(value = Value.YES.name)),
      )

      val exception = assertThrows<ConflictException> {
        assessmentVersionService.sign(signedVersion, counterSignType, UserDetails())
      }
      assertEquals("The current assessment version is already ${signedVersion.tag.name}.", exception.message)

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }

    @Test
    fun `it throws an exception when the assessment is not completed`() {
      val exception = assertThrows<ConflictException> {
        assessmentVersionService.sign(assessmentVersion, CounterSignType.SELF, UserDetails())
      }
      assertEquals("The current assessment version is not completed.", exception.message)

      verify(exactly = 0) { assessmentVersionRepository.save(any()) }
      verify(exactly = 0) { assessmentVersionAuditRepository.save(any()) }
    }
  }
}
