package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.CounterSignType
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.TransferAssociationRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Field
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.datamapping.Value
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.criteria.AssessmentVersionCriteria
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.AssessmentVersion
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Tag
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentVersionService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val assessmentVersionService: AssessmentVersionService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String): OasysAssessment {
    return assessmentService.create()
      .run {
        oasysAssessmentRepository.save(
          OasysAssessment(
            oasysAssessmentPk = oasysAssessmentPk,
            assessment = this,
          ),
        )
      }
      .also { log.info("Assessment created for OASys PK $oasysAssessmentPk") }
  }

  fun findOrCreateAssessment(oasysAssessmentPk: String): OasysAssessment {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk) ?: createAssessmentWithOasysId(
      oasysAssessmentPk,
    )
  }

  fun findOrNull(oasysAssessmentPk: String): OasysAssessment? {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
  }

  fun find(oasysAssessmentPk: String): OasysAssessment {
    return findOrNull(oasysAssessmentPk)
      ?: throw OasysAssessmentNotFoundException(oasysAssessmentPk)
  }

  private fun associate(oasysAssessmentPk: String, previousOasysAssessmentPk: String): OasysAssessment {
    return find(previousOasysAssessmentPk).let {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = it.assessment,
      )
      oasysAssessmentRepository.save(oasysAssessment)
    }
  }

  fun associateExistingOrCreate(oasysAssessmentPk: String, previousOasysAssessmentPk: String? = null): Assessment {
    return findOrNull(oasysAssessmentPk)?.let { throw OasysAssessmentAlreadyExistsException(oasysAssessmentPk) }
      ?: run {
        previousOasysAssessmentPk?.let {
          associate(oasysAssessmentPk, previousOasysAssessmentPk).assessment
        } ?: createAssessmentWithOasysId(oasysAssessmentPk).assessment
      }.also {
        log.info("Associated OASys assessment PK $oasysAssessmentPk with SAN assessment ${it.uuid}")
      }
  }

  fun sign(oasysAssessmentPk: String, counterSignType: CounterSignType): AssessmentVersion {
    val oasysAssessment = find(oasysAssessmentPk)

    val assessmentVersion = AssessmentVersionCriteria(oasysAssessment.assessment.uuid, Tag.validatedTags())
      .let { assessmentVersionService.find(it) }

    if (assessmentVersion.answers[Field.ASSESSMENT_COMPLETE.lower]?.value != Value.YES.name) {
      throw ConflictException("The current assessment version is not completed.")
    }

    val tag = when (counterSignType) {
      CounterSignType.SELF -> Tag.SELF_SIGNED
      CounterSignType.COUNTERSIGN -> Tag.AWAITING_COUNTERSIGN
    }

    if (assessmentVersion.tag == tag) {
      throw ConflictException("The current assessment version is already ${tag.name}.")
    }

    return assessmentVersionService.cloneAndTag(assessmentVersion, tag)
  }

  fun lock(oasysAssessmentPk: String): AssessmentVersion {
    val oasysAssessment = find(oasysAssessmentPk)

    val assessmentVersion = AssessmentVersionCriteria(oasysAssessment.assessment.uuid, Tag.validatedTags())
      .let { assessmentVersionService.find(it) }

    if (assessmentVersion.tag == Tag.LOCKED_INCOMPLETE) {
      throw ConflictException("OASys assessment with ID $oasysAssessmentPk has already been locked")
    }

    return assessmentVersionService.cloneAndTag(assessmentVersion, Tag.LOCKED_INCOMPLETE)
  }

  @Transactional
  fun transferAssociation(request: List<TransferAssociationRequest>) {
    request.forEach {
      with(it) {
        find(oldOasysAssessmentPK).let { oldAssociation ->
          OasysAssessment(
            oasysAssessmentPk = newOasysAssessmentPK,
            assessment = oldAssociation.assessment,
          ).also { newAssociation ->
            oasysAssessmentRepository.findByOasysAssessmentPk(newAssociation.oasysAssessmentPk)
              ?.run { throw OasysAssessmentAlreadyExistsException(oasysAssessmentPk) }

            oasysAssessmentRepository.save(newAssociation)
            oasysAssessmentRepository.delete(oldAssociation)

            log.info("Successfully transferred association for ${oldAssociation.oasysAssessmentPk} to ${newAssociation.oasysAssessmentPk}")
          }
        }
      }
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
