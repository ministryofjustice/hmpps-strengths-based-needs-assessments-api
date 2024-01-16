-- Drop deprecated tables
DROP TABLE IF EXISTS assessment_form_info;
DROP TABLE IF EXISTS oasys_sessions;
DROP TABLE IF EXISTS assessments;
DROP TABLE IF EXISTS subjects;

-- Create new table structure to decouple oasys assessments and sessions
CREATE TABLE assessments (
    id                      serial          PRIMARY KEY,
    uuid                    uuid            NOT NULL UNIQUE,
    created_at              timestamp       NOT NULL
);

CREATE TABLE IF NOT EXISTS assessments_versions (
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    assessment_uuid     uuid            NOT NULL,
    tag                 varchar(64)     NOT NULL,
    answers             jsonb           NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES assessments (uuid)
);

CREATE TABLE assessments_form_info
(
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    assessment_uuid     uuid            NOT NULL,
    form_name           varchar(64)     NOT NULL,
    form_version        varchar(64)     NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES assessments(uuid)
);


CREATE TABLE IF NOT EXISTS oasys_assessments (
    id                      serial          PRIMARY KEY,
    uuid                    uuid            NOT NULL UNIQUE,
    created_at              timestamp       NOT NULL,
    assessment_uuid         uuid            NOT NULL,
    oasys_assessment_pk     varchar(64)     NOT NULL,
    CONSTRAINT assessment_uuid_oasys_pk UNIQUE (assessment_uuid, oasys_assessment_pk),
    FOREIGN KEY (assessment_uuid) REFERENCES assessments (uuid)
);

CREATE TABLE oasys_sessions (
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    user_id             varchar(128)    NOT NULL,
    user_access         varchar(64)    NOT NULL,
    user_display_name   varchar(256)    NOT NULL,
    link_status         varchar(32)     NOT NULL,
    link_uuid           uuid            NOT NULL,
    assessment_uuid     uuid            NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES oasys_assessments (uuid)
);
