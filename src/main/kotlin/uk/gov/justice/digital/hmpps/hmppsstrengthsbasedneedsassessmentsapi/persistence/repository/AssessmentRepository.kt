package uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppsstrengthsbasedneedsassessmentsapi.persistence.entity.Assessment
import java.util.UUID

@Repository
interface AssessmentRepository : JpaRepository<Assessment, Long> {
  fun findByUuid(uuid: UUID): Assessment?
  @Query(
    value = """
        SELECT a.*
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
    """,
    nativeQuery = true,
  )
  fun findAllToMigrate(
    pageable: Pageable,
  ): Page<Assessment>

  @Query(
    value = """
        SELECT a.*
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
          AND a.id NOT IN (:ignoreIds)
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
          AND a.id NOT IN (:ignoreIds)
    """,
    nativeQuery = true,
  )
  fun findAllToMigrateExcludingIds(
    @Param("ignoreIds") ignoreIds: Set<Long>,
    pageable: Pageable,
  ): Page<Assessment>

  @Query(
    value = """
        SELECT a.*
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
          AND a.id IN (:assessmentIds)
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM assessments a
        LEFT JOIN migration_log ml ON ml.entity_type = 'SAN' AND a.id = ml.entity_id
        WHERE ml.id IS NULL
          AND a.id IN (:assessmentIds)
    """,
    nativeQuery = true,
  )
  fun findAllToMigrateById(
    @Param("assessmentIds") assessmentIds: List<Long>,
    pageable: Pageable,
  ): Page<Assessment>
}
