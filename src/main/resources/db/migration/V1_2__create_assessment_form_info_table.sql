CREATE TABLE IF NOT EXISTS assessment_form_info
(
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    form_name           varchar(64)     NOT NULL,
    form_version        varchar(64)     NOT NULL,
    assessment_uuid     uuid            NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES assessments(uuid)
);
