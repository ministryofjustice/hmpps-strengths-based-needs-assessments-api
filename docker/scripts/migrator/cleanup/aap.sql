DELETE FROM "assessment-platform".assessment a
    USING "assessment-platform".timeline t
WHERE t.assessment_uuid = a.uuid
  AND t.custom_type = 'MIGRATED_SAN';
