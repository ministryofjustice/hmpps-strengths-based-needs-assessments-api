UPDATE assessments_versions SET tag = CASE WHEN tag = '' THEN 'UNVALIDATED' ELSE UPPER(tag) END;
