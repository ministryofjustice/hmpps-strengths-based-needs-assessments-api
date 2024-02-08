DELETE FROM oasys_assessments WHERE true;
DELETE FROM assessments_form_info WHERE true;
DELETE FROM assessments_versions WHERE true;
DELETE FROM assessments WHERE true;

INSERT INTO assessments (uuid, created_at)
VALUES ('7507b51e-a6e1-4820-9298-84c717cbacfc', '2024-01-01 12:00:00.000');

INSERT INTO oasys_assessments (uuid, created_at, assessment_uuid, oasys_assessment_pk)
VALUES ('212d2061-d198-4f18-b779-537cb01a437c', '2024-01-01 12:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', '0000000001'),
       ('27a06c16-3cf9-43e7-a36e-e4973c343ce5', '2024-01-01 12:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', '0000000002');

INSERT INTO assessments_form_info (uuid, created_at, assessment_uuid, form_name, form_version)
VALUES ('50ff0144-9c01-4808-a4a9-d2082ffc133a', '2024-01-01 12:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', 'san', '1.0');

INSERT INTO assessments_versions (uuid, created_at, assessment_uuid, tag, answers, oasys_equivalent)
VALUES ('a6b76ab6-3caf-4298-a3eb-5e033a0cf379', '2024-01-01 12:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', 'unvalidated', '{"current_accommodation": {"type": "RADIO", "value": "SETTLED", "values": null, "options": [{"text": "Settled", "value": "SETTLED"}, {"text": "Temporary", "value": "TEMPORARY"}, {"text": "No accommodation", "value": "NO_ACCOMMODATION"}], "description": "What is [subject]''s current accommodation?"}}' FORMAT JSON, '{"foo": "BAR"}' FORMAT JSON),
       ('d9e9d8ff-584e-46a2-a698-d13ba1295a62', '2024-01-01 12:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', 'validated', '{"current_accommodation": {"type": "RADIO", "value": "SETTLED", "values": null, "options": [{"text": "Settled", "value": "SETTLED"}, {"text": "Temporary", "value": "TEMPORARY"}, {"text": "No accommodation", "value": "NO_ACCOMMODATION"}], "description": "What is [subject]''s current accommodation?"}}' FORMAT JSON, '{"foo": "BAR"}' FORMAT JSON),
       ('6e1d9bc7-7df9-425a-aaf4-35cb699a7bf2', '2024-01-01 10:00:00.000', '7507b51e-a6e1-4820-9298-84c717cbacfc', 'validated', '{"current_accommodation": {"type": "RADIO", "value": "NO_ACCOMMODATION", "values": null, "options": [{"text": "Settled", "value": "SETTLED"}, {"text": "Temporary", "value": "TEMPORARY"}, {"text": "No accommodation", "value": "NO_ACCOMMODATION"}], "description": "What is [subject]''s current accommodation?"}}' FORMAT JSON, '{"foo": "BAR"}' FORMAT JSON);
