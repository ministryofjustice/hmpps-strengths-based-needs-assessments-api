CREATE TABLE assessment_events
(
    id               SERIAL PRIMARY KEY,
    uuid             UUID      NOT NULL DEFAULT gen_random_uuid(),
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    assessment_uuid  UUID      NOT NULL,
    user_details     JSONB     NOT NULL,
    event_data JSONB,

    CONSTRAINT fk_assessment_uuid
        FOREIGN KEY (assessment_uuid)
            REFERENCES assessments (uuid)
            ON DELETE RESTRICT
);

CREATE TABLE assessment_aggregates
(
    id              SERIAL PRIMARY KEY,
    uuid            UUID      NOT NULL DEFAULT gen_random_uuid(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    events_from     TIMESTAMP NOT NULL,
    events_to       TIMESTAMP NOT NULL,
    assessment_uuid UUID      NOT NULL,
    answers         JSONB     NOT NULL DEFAULT '{}',
    CONSTRAINT fk_assessment
        FOREIGN KEY (assessment_uuid)
            REFERENCES assessments (uuid)
            ON DELETE RESTRICT
);

CREATE UNIQUE INDEX idx_assessment_aggregates_uuid ON assessment_aggregates (uuid);
