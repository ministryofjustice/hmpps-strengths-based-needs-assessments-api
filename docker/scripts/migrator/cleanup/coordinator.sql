BEGIN;

DELETE FROM "coordinator".oasys_version
    USING "coordinator".oasys_associations
WHERE oasys_associations.entity_uuid = oasys_version.entity_uuid
  AND oasys_associations.entity_type = 'AAP_SAN'
  AND oasys_associations.oasys_assessment_pk IN (SELECT b.oasys_assessment_pk FROM coordinator.oasys_associations b where b.entity_type = 'ASSESSMENT');

DELETE FROM "coordinator".oasys_associations
WHERE entity_type = 'AAP_SAN'
  AND oasys_assessment_pk IN (SELECT b.oasys_assessment_pk FROM coordinator.oasys_associations b where b.entity_type = 'ASSESSMENT');

COMMIT;
