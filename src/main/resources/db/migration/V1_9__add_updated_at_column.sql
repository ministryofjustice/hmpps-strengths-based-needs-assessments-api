ALTER TABLE assessments_versions ADD COLUMN updated_at timestamp;
UPDATE assessments_versions SET updated_at = created_at;
ALTER TABLE assessments_versions ALTER COLUMN updated_at SET NOT NULL;
