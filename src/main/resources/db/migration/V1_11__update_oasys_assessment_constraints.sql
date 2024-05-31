WITH duplicates AS (SELECT count(1), oasys_assessment_pk
                    FROM oasys_assessments
                    GROUP BY oasys_assessment_pk
                    HAVING count(1) > 1),
     keep AS (SELECT MAX(oa.id) AS id, oa.oasys_assessment_pk
              FROM oasys_assessments oa
                       JOIN duplicates d ON d.oasys_assessment_pk = oa.oasys_assessment_pk
              GROUP BY oa.oasys_assessment_pk)
DELETE
FROM oasys_assessments
WHERE oasys_assessment_pk IN (SELECT oasys_assessment_pk FROM duplicates)
  AND id NOT IN (SELECT id FROM keep);

ALTER TABLE oasys_assessments
    ADD CONSTRAINT oasys_assessment_pk_unique UNIQUE (oasys_assessment_pk);
