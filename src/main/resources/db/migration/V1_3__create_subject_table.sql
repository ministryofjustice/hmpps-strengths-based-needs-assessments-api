CREATE TABLE IF NOT EXISTS subjects
(
    id                  serial          PRIMARY KEY,
    uuid                uuid            NOT NULL UNIQUE,
    created_at          timestamp       NOT NULL,
    crn                 varchar(16)     NOT NULL UNIQUE
);

ALTER TABLE assessments ADD subject_uuid uuid;
ALTER TABLE assessments ADD FOREIGN KEY (subject_uuid) REFERENCES subjects (uuid);
