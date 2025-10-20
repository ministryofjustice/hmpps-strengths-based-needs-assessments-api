DO $$
    DECLARE
        sections JSONB := '{
      "accommodation": "Accommodation",
      "alcohol_use": "Alcohol use",
      "employment_education": "Employment and education",
      "finance": "Finances",
      "drug_use": "Drug use",
      "health_wellbeing": "Health and wellbeing",
      "personal_relationships_community": "Personal relationships and community",
      "thinking_behaviours_attitudes": "Thinking, behaviours and attitudes"
    }';
        code TEXT;
        display_name TEXT;
    BEGIN
        FOR code, display_name IN
            SELECT key, value
            FROM jsonb_each_text(sections)
            LOOP
                EXECUTE format($sql$
            UPDATE assessments_versions
            SET answers = answers || jsonb_build_object(
                '%1$s_background_section_complete',
                jsonb_build_object(
                    'type', 'RADIO',
                    'value', 'YES',
                    'values', to_jsonb(NULL::text),
                    'options', jsonb_build_array(
                        jsonb_build_object('text', 'Yes', 'value', 'YES'),
                        jsonb_build_object('text', 'No', 'value', 'NO')
                    ),
                    'collection', to_jsonb(NULL::text),
                    'description', 'Is the %2$s background section complete?'
                ),
                '%1$s_practitioner_analysis_section_complete',
                jsonb_build_object(
                    'type', 'RADIO',
                    'value', 'YES',
                    'values', to_jsonb(NULL::text),
                    'options', jsonb_build_array(
                        jsonb_build_object('text', 'Yes', 'value', 'YES'),
                        jsonb_build_object('text', 'No', 'value', 'NO')
                    ),
                    'collection', to_jsonb(NULL::text),
                    'description', 'Is the %2$s practitioner analysis section complete?'
                ),
                '%1$s_is_%1$s_analysis_user_submitted',
                jsonb_build_object(
                    'type', 'RADIO',
                    'value', 'YES',
                    'values', to_jsonb(NULL::text),
                    'options', jsonb_build_array(
                        jsonb_build_object('text', 'Yes', 'value', 'YES'),
                        jsonb_build_object('text', 'No', 'value', 'NO')
                            ),
                    'collection', to_jsonb(NULL::text),
                    'description', 'Has the user submitted the page?'
                )
            )
            WHERE NOT (answers ? '%1$s_background_section_complete')
              AND answers->'%1$s_section_complete'->>'value' = 'YES';
        $sql$, code, display_name);
            END LOOP;
    END $$;
