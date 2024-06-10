package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.controller.request.TransferAssociationRequest
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.entity.OasysAssessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.persistence.repository.OasysAssessmentRepository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentAlreadyExistsException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.oasys.service.exception.OasysAssessmentNotFoundException
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.AssessmentService
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.service.exception.ConflictException

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String, regionPrisonCode: String?): OasysAssessment {
    return OasysAssessment(
      oasysAssessmentPk = oasysAssessmentPk,
      assessment = assessmentService.create(),
      regionPrisonCode = regionPrisonCode,
    )
      .run(oasysAssessmentRepository::save)
      .also { log.info("Assessment created for OASys PK $oasysAssessmentPk") }
  }

  fun findOrNull(oasysAssessmentPk: String): OasysAssessment? {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
  }

  fun find(oasysAssessmentPk: String): OasysAssessment {
    return findOrNull(oasysAssessmentPk)
      ?: throw OasysAssessmentNotFoundException(oasysAssessmentPk)
  }

  private fun associate(
    oasysAssessmentPk: String,
    previousOasysAssessmentPk: String,
    regionPrisonCode: String?,
  ): OasysAssessment {
    return OasysAssessment(
      oasysAssessmentPk = oasysAssessmentPk,
      assessment = find(previousOasysAssessmentPk).assessment,
      regionPrisonCode = regionPrisonCode,
    ).run(oasysAssessmentRepository::save)
  }

  fun associateExistingOrCreate(
    oasysAssessmentPk: String,
    previousOasysAssessmentPk: String? = null,
    regionPrisonCode: String? = null,
  ): Assessment {
    return findOrNull(oasysAssessmentPk)?.let { throw OasysAssessmentAlreadyExistsException(oasysAssessmentPk) }
      ?: run {
        previousOasysAssessmentPk?.let {
          associate(oasysAssessmentPk, previousOasysAssessmentPk, regionPrisonCode).assessment
        } ?: createAssessmentWithOasysId(oasysAssessmentPk, regionPrisonCode).assessment
      }.also {
        log.info("Associated OASys assessment PK $oasysAssessmentPk with SAN assessment ${it.uuid}")
      }
  }

  @Transactional
  fun transferAssociation(request: List<TransferAssociationRequest>) {
    request.forEach {
      find(it.oldOasysAssessmentPK).let { oldAssociation ->
        OasysAssessment(
          oasysAssessmentPk = it.newOasysAssessmentPK,
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

  fun softDelete(oasysAssessment: OasysAssessment): OasysAssessment {
    if (oasysAssessment.deleted) {
      throw ConflictException("OASys assessment ${oasysAssessment.oasysAssessmentPk} has already been soft-deleted.")
    }

    return oasysAssessment.apply { deleted = true }
      .run(oasysAssessmentRepository::save)
      .also {
        log.info("Successfully soft-deleted OASys assessment PK ${oasysAssessment.oasysAssessmentPk}")
      }
  }

  fun undelete(oasysAssessmentPk: String): OasysAssessment {
    return oasysAssessmentRepository.findDeletedByOasysAssessmentPk(oasysAssessmentPk)
      ?.apply { deleted = false }
      ?.run(oasysAssessmentRepository::save)
      ?.also { log.info("Successfully undeleted OASys assessment PK ${it.oasysAssessmentPk}") }
      ?: run {
        find(oasysAssessmentPk)
        throw ConflictException("Cannot undelete OASys assessment PK $oasysAssessmentPk because it is not deleted.")
      }
  }

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
}
