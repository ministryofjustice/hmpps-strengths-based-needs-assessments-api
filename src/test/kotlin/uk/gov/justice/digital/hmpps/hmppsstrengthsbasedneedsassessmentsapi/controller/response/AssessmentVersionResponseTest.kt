package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.controller.response

import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("AssessmentVersionResponse")
class AssessmentVersionResponseTest {
  private val now: LocalDateTime = LocalDateTime.now()

  private val version = AssessmentVersion(
    uuid = UUID.randomUUID(),
    createdAt = now,
    updatedAt = now,
    tag = Tag.UNSIGNED,
    versionNumber = 1,
  )

  private val versions = listOf(
    AssessmentVersion(
      uuid = UUID.randomUUID(),
      createdAt = now,
      updatedAt = now,
      tag = Tag.UNSIGNED,
      versionNumber = 1,
    ),
    AssessmentVersion(
      uuid = UUID.randomUUID(),
      createdAt = now.minusDays(1),
      updatedAt = now.minusDays(1),
      tag = Tag.UNSIGNED,
      versionNumber = 2,
    ),
  )

  @Nested
  @DisplayName("from")
  inner class From {
    @Test
    fun `it returns a response in the correct format for a single version`() {
      val response = AssessmentVersionResponse.from(version)

      assertThat(response.uuid).isEqualTo(version.uuid)
      assertThat(response.createdAt.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(version.createdAt.truncatedTo(ChronoUnit.MILLIS))
      assertThat(response.updatedAt.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(version.updatedAt.truncatedTo(ChronoUnit.MILLIS))
      assertThat(response.tag).isEqualTo(version.tag)
      assertThat(response.versionNumber).isEqualTo(version.versionNumber)
    }
  }

  @Nested
  @DisplayName("fromAll")
  inner class FromAll {
    @Test
    fun `it returns a response in the correct format for list of versions`() {
      val response = AssessmentVersionResponse.fromAll(versions)

      assertThat(response).hasSize(2)

      with(response.getOrNull(0)) {
        assertThat(this).isNotNull()
        assertThat(this?.uuid).isEqualTo(versions[0].uuid)
        assertThat(this?.createdAt?.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(versions[0].createdAt.truncatedTo(ChronoUnit.MILLIS))
        assertThat(this?.updatedAt?.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(versions[0].updatedAt.truncatedTo(ChronoUnit.MILLIS))
        assertThat(this?.tag).isEqualTo(versions[0].tag)
        assertThat(this?.versionNumber).isEqualTo(versions[0].versionNumber)
      }

      with(response.getOrNull(1)) {
        assertThat(this).isNotNull()
        assertThat(this?.uuid).isEqualTo(versions[1].uuid)
        assertThat(this?.createdAt?.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(versions[1].createdAt.truncatedTo(ChronoUnit.MILLIS))
        assertThat(this?.updatedAt?.truncatedTo(ChronoUnit.MILLIS)).isEqualTo(versions[1].updatedAt.truncatedTo(ChronoUnit.MILLIS))
        assertThat(this?.tag).isEqualTo(versions[1].tag)
        assertThat(this?.versionNumber).isEqualTo(versions[1].versionNumber)
      }
    }

    @Test
    fun `it returns an empty list`() {
      val response = AssessmentVersionResponse.fromAll(emptyList())
      assertThat(response).isEmpty()
    }
  }
}
