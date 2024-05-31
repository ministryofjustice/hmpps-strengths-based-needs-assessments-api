CREATE TABLE assessment_version_audit
(
    id                      serial      PRIMARY KEY,
    uuid                    uuid        NOT NULL,
    created_at              timestamp   NOT NULL,
    assessment_version_uuid uuid        NOT NULL,
    user_details            jsonb       NOT NULL,
    status_from             VARCHAR(64),
    status_to               VARCHAR(64),
);

ALTER TABLE assessment_version_audit
    ADD CONSTRAINT FK_ASSESSMENT_VERSION_AUDIT_ON_ASSESSMENT_VERSION_UUID FOREIGN KEY (assessment_version_uuid) REFERENCES assessments_versions (uuid);
