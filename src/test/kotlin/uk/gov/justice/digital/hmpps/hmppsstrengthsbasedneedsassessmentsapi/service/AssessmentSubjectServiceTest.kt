package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.OasysGender
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.SubjectDetailsRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentSubject
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Gender
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Location
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository.AssessmentSubjectRepository
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentSubjectService")
class AssessmentSubjectServiceTest {
  private val assessmentSubjectRepository: AssessmentSubjectRepository = mockk()
  private val assessmentSubjectService = AssessmentSubjectService(assessmentSubjectRepository)

  @BeforeEach
  fun setUp() {
    clearAllMocks()
  }

  @Nested
  @DisplayName("find")
  inner class Find {
    @Test
    fun `it returns the subject for a given assessment`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val assessmentSubject = AssessmentSubject()

      every {
        assessmentSubjectRepository.findByAssessment(assessment)
      } returns assessmentSubject

      val result = assessmentSubjectService.find(assessment)
      assertThat(result).isEqualTo(assessmentSubject)
    }

    @Test
    fun `it returns null when no subject found for a given assessment`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())

      every {
        assessmentSubjectRepository.findByAssessment(assessment)
      } returns null

      val result = assessmentSubjectService.find(assessment)
      assertThat(result).isNull()
    }
  }

  @Nested
  @DisplayName("create")
  inner class Create {
    @Test
    fun `it creates the subject for a given assessment`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val subjectDetails = SubjectDetailsRequest(
        crn = "A123456",
        pnc = "1234567890/0",
        nomisId = "1234567",
        givenName = "Paul",
        familyName = "Whitfield",
        gender = OasysGender.MALE,
        location = Location.COMMUNITY,
        sexuallyMotivatedOffenceHistory = "Yes",
      )

      every { assessmentSubjectRepository.save(any()) } returnsArgument 0

      val result = assessmentSubjectService.create(assessment, subjectDetails)

      assertThat(result.assessment).isEqualTo(assessment)
      assertThat(result.subjectDetails?.crn).isEqualTo(subjectDetails.crn)
      assertThat(result.subjectDetails?.pnc).isEqualTo(subjectDetails.pnc)
      assertThat(result.subjectDetails?.nomisId).isEqualTo(subjectDetails.nomisId)
      assertThat(result.subjectDetails?.givenName).isEqualTo(subjectDetails.givenName)
      assertThat(result.subjectDetails?.familyName).isEqualTo(subjectDetails.familyName)
      assertThat(result.subjectDetails?.gender).isEqualTo(Gender.MALE)
      assertThat(result.subjectDetails?.location).isEqualTo(subjectDetails.location)
      assertThat(result.subjectDetails?.sexuallyMotivatedOffenceHistory).isEqualTo(true)
    }
  }

  @Nested
  @DisplayName("updateOrCreate")
  inner class UpdateOrCreate {
    @Test
    fun `it creates the subject for a given assessment when none already exists`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val subjectDetails = SubjectDetailsRequest(
        crn = "A123456",
        pnc = "1234567890/0",
        nomisId = "1234567",
        givenName = "Paul",
        familyName = "Whitfield",
        gender = OasysGender.MALE,
        location = Location.COMMUNITY,
        sexuallyMotivatedOffenceHistory = "Yes",
      )

      every { assessmentSubjectRepository.findByAssessment(any()) } returns null
      every { assessmentSubjectRepository.save(any()) } returnsArgument 0

      val result = assessmentSubjectService.updateOrCreate(assessment, subjectDetails)

      assertThat(result.assessment).isEqualTo(assessment)
      assertThat(result.subjectDetails?.crn).isEqualTo(subjectDetails.crn)
      assertThat(result.subjectDetails?.pnc).isEqualTo(subjectDetails.pnc)
      assertThat(result.subjectDetails?.nomisId).isEqualTo(subjectDetails.nomisId)
      assertThat(result.subjectDetails?.givenName).isEqualTo(subjectDetails.givenName)
      assertThat(result.subjectDetails?.familyName).isEqualTo(subjectDetails.familyName)
      assertThat(result.subjectDetails?.gender).isEqualTo(Gender.MALE)
      assertThat(result.subjectDetails?.location).isEqualTo(subjectDetails.location)
      assertThat(result.subjectDetails?.sexuallyMotivatedOffenceHistory).isEqualTo(true)
    }

    @Test
    fun `updates an existing subject where it already exists`() {
      val assessment = Assessment(id = 1, uuid = UUID.randomUUID())
      val subjectDetails = SubjectDetailsRequest(
        crn = "A123456",
        pnc = "1234567890/0",
        nomisId = "1234567",
        givenName = "Paul",
        familyName = "Whitfield",
        gender = OasysGender.MALE,
        location = Location.COMMUNITY,
        sexuallyMotivatedOffenceHistory = "Yes",
      )

      val assessmentSubject = AssessmentSubject(id = 1, assessment = assessment)

      every { assessmentSubjectRepository.findByAssessment(assessment) } returns assessmentSubject
      every { assessmentSubjectRepository.save(assessmentSubject) } returnsArgument 0

      val result = assessmentSubjectService.updateOrCreate(assessment, subjectDetails)

      assertThat(result.assessment).isEqualTo(assessment)
      assertThat(result.subjectDetails?.crn).isEqualTo(subjectDetails.crn)
      assertThat(result.subjectDetails?.pnc).isEqualTo(subjectDetails.pnc)
      assertThat(result.subjectDetails?.nomisId).isEqualTo(subjectDetails.nomisId)
      assertThat(result.subjectDetails?.givenName).isEqualTo(subjectDetails.givenName)
      assertThat(result.subjectDetails?.familyName).isEqualTo(subjectDetails.familyName)
      assertThat(result.subjectDetails?.gender).isEqualTo(Gender.MALE)
      assertThat(result.subjectDetails?.location).isEqualTo(subjectDetails.location)
      assertThat(result.subjectDetails?.sexuallyMotivatedOffenceHistory).isEqualTo(true)
    }
  }
}
