CREATE TABLE IF NOT EXISTS migration_log
(
    id          serial PRIMARY KEY,
    entity_type varchar(128) NOT NULL,
    entity_id   integer DEFAULT NULL,
    entity_uuid uuid DEFAULT NULL,
    aap_uuid    uuid DEFAULT NULL,
    migrated_at timestamp DEFAULT NULL
);
