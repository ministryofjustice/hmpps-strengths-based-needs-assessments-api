package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.request.UpdateAssessmentAnswersRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Answer
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AnswerType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentVersionRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.AssessmentVersionNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentVersionService")
class AssessmentVersionServiceTest {
  private val assessmentService: AssessmentService = mockk()
  private val assessmentVersionRepository: AssessmentVersionRepository = mockk()
  private val dataMappingService: DataMappingService = mockk()
  private val assessmentVersionService = AssessmentVersionService(
    assessmentService = assessmentService,
    assessmentVersionRepository = assessmentVersionRepository,
    dataMappingService = dataMappingService,
  )

  private val tag = "test"

  private val firstAssessmentVersion = AssessmentVersion(
    tag = tag,
    createdAt = LocalDateTime.now().minusDays(1).minusHours(1),
    answers = mapOf("foo" to Answer(AnswerType.TEXT, "Foo question", null, "Foo answer", null)),
  )

  private val secondAssessmentVersion = AssessmentVersion(
    tag = tag,
    createdAt = LocalDateTime.now().minusHours(1),
    answers = mapOf("test" to Answer(value = "val")),
  )

  @Nested
  @DisplayName("getPreviousOrCreate")
  inner class GetPreviousOrCreate {
    @Test
    fun `it returns the previous assessment version if it was created today`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> =
        PageImpl(listOf(secondAssessmentVersion))

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
      assertThat(result.tag).isEqualTo(tag)
      assertThat(result.uuid).isEqualTo(secondAssessmentVersion.uuid)
      assertThat(result.answers["test"]?.value).isEqualTo("val")
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

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
      assertThat(result.uuid).isNotEqualTo(firstAssessmentVersion.uuid)
      assertThat(result.tag).isEqualTo(tag)
      assertThat(result.answers["foo"]?.value).isEqualTo("Foo answer")
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

      val result = assessmentVersionService.getPreviousOrCreate(tag, assessment)
      assertThat(result.tag).isEqualTo(tag)
      assertThat(result.answers).isEmpty()
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

      val specification = AssessmentVersionCriteria(assessmentUuid = assessment.uuid, tag = tag)
      val result = assessmentVersionService.find(specification)

      assertThat(result).isEqualTo(firstAssessmentVersion)
    }

    @Test
    fun `it throws when there is no assessment version for a given tag`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentVersions: Page<AssessmentVersion> = PageImpl(emptyList())

      every {
        assessmentVersionRepository.findAll(
          any<Specification<AssessmentVersion>>(),
          any<PageRequest>(),
        )
      } returns assessmentVersions

      val specification = AssessmentVersionCriteria(assessmentUuid = assessment.uuid, tag = tag)
      assertThrows<AssessmentVersionNotFoundException> { assessmentVersionService.find(specification) }
    }
  }

  @Nested
  @DisplayName("updateAnswers")
  inner class UpdateAnswers {
    @Test
    fun `it updates answers for a given assessment`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())

      val assessmentVersions: Page<AssessmentVersion> = PageImpl(
        listOf(
          AssessmentVersion(
            tag = tag,
            createdAt = LocalDateTime.of(2023, 12, 1, 12, 0),
            answers = mapOf(
              "foo" to Answer(AnswerType.TEXT, "Foo question", null, "not updated", null),
              "bar" to Answer(AnswerType.TEXT, "Bar question", null, "not updated", null),
              "baz" to Answer(AnswerType.TEXT, "Baz question", null, "should be removed", null),
            ),
          ),
          AssessmentVersion(
            tag = tag,
            createdAt = LocalDateTime.of(2023, 6, 1, 12, 0),
            answers = emptyMap(),
          ),
        ),
      )

      val request = UpdateAssessmentAnswersRequest(
        tags = listOf(tag),
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
      every { assessmentService.findByUuid(assessment.uuid) } returns assessment
      every { dataMappingService.getOasysEquivalent(any()) } returns oasysEquivalents

      val savedVersion = slot<AssessmentVersion>()
      every { assessmentVersionRepository.save(capture(savedVersion)) } returnsArgument 0

      assessmentVersionService.updateAnswers(assessment.uuid, request)

      assertThat(savedVersion.captured.assessment).isEqualTo(assessment)
      assertThat(savedVersion.captured.tag).isEqualTo(tag)
      assertThat(savedVersion.captured.answers["foo"]?.value).isEqualTo("updated")
      assertThat(savedVersion.captured.answers["bar"]?.value).isEqualTo("not updated")
      assertThat(savedVersion.captured.answers["baz"]).isNull()
      assertThat(savedVersion.captured.oasys_equivalent).isEqualTo(oasysEquivalents)
    }
  }
}
