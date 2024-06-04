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

@Service
class OasysAssessmentService(
  val assessmentService: AssessmentService,
  val oasysAssessmentRepository: OasysAssessmentRepository,
) {
  fun createAssessmentWithOasysId(oasysAssessmentPk: String, regionPrisonCode: String?): OasysAssessment {
    return assessmentService.create()
      .run {
        oasysAssessmentRepository.save(
          OasysAssessment(
            oasysAssessmentPk = oasysAssessmentPk,
            assessment = this,
            regionPrisonCode = regionPrisonCode,
          ),
        )
      }
      .also { log.info("Assessment created for OASys PK $oasysAssessmentPk") }
  }

  fun findOrNull(oasysAssessmentPk: String): OasysAssessment? {
    return oasysAssessmentRepository.findByOasysAssessmentPk(oasysAssessmentPk)
  }

  fun find(oasysAssessmentPk: String): OasysAssessment {
    return findOrNull(oasysAssessmentPk)
      ?: throw OasysAssessmentNotFoundException(oasysAssessmentPk)
  }

  private fun associate(oasysAssessmentPk: String, previousOasysAssessmentPk: String, regionPrisonCode: String?): OasysAssessment {
    return find(previousOasysAssessmentPk).let {
      val oasysAssessment = OasysAssessment(
        oasysAssessmentPk = oasysAssessmentPk,
        assessment = it.assessment,
        regionPrisonCode = regionPrisonCode,
      )
      oasysAssessmentRepository.save(oasysAssessment)
    }
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
