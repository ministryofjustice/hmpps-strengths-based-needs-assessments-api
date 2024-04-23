CREATE TABLE IF NOT EXISTS assessment_subject
(
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    assessment_uuid     uuid            NOT NULL UNIQUE,
    subject_details     jsonb           NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES assessments(uuid)
);
