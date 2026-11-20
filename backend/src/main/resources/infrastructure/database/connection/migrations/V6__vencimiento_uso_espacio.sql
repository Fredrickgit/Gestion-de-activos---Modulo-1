ALTER TABLE espacio ADD COLUMN uso_termina_en timestamptz;

CREATE INDEX idx_espacio_uso_termina_en ON espacio (uso_termina_en, id)
    WHERE uso_termina_en IS NOT NULL;
