with versionNumbers as (
    SELECT ROW_NUMBER() OVER (PARTITION BY av.assessment_uuid ORDER BY av.created_at ASC) - 1 as new_version_number, av.*
    FROM assessments_versions av
)
update assessments_versions av
SET version_number = versionNumbers.new_version_number
    FROM versionNumbers
WHERE av.uuid = versionNumbers.uuid;
select * from assessments_versions where assessment_uuid = '3a866cf5-070e-484e-86d8-97e6da471607';

ALTER TABLE assessments_versions ADD CONSTRAINT assessment_uuid_version_number UNIQUE (assessment_uuid, version_number);
