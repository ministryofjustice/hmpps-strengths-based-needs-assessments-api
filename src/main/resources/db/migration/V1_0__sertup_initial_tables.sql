CREATE TABLE IF NOT EXISTS assessments
(
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    oasys_assessment_id varchar(128)    NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS oasys_sessions
(
    id              serial          PRIMARY KEY,
    uuid            uuid            NOT NULL UNIQUE,
    created_at      timestamp       NOT NULL,
    user_id         varchar(128)    NOT NULL,
    user_access     varchar(256)    NOT NULL,
    assessment_uuid uuid            NOT NULL,
    link_status     varchar(32)     NOT NULL,
    link_uuid       uuid            NOT NULL,
    FOREIGN KEY (assessment_uuid) REFERENCES assessments (uuid)
);
