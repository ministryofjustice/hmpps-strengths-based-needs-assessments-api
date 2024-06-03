package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.formconfig.FormConfig
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "assessments")
data class Assessment(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long? = null,

  @Column(name = "uuid")
  val uuid: UUID = UUID.randomUUID(),

  @Column(name = "created_at")
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @OneToOne(optional = true, mappedBy = "assessment", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
  var info: AssessmentFormInfo? = null,

  @OneToMany(mappedBy = "assessment", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
  var assessmentVersions: List<AssessmentVersion> = listOf(),

  @OneToMany(mappedBy = "assessment", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
  var oasysAssessments: List<OasysAssessment> = listOf(),
) {
  companion object {
    fun new(formConfig: FormConfig): Assessment {
      return Assessment()
        .apply {
          info = AssessmentFormInfo(
            formName = formConfig.name,
            formVersion = formConfig.version,
            assessment = this,
          )

          assessmentVersions = listOf(
            AssessmentVersion(assessment = this, versionNumber = 0, tag = Tag.UNVALIDATED),
            AssessmentVersion(assessment = this, versionNumber = 1, tag = Tag.UNSIGNED),
          )
        }
    }
  }
}
