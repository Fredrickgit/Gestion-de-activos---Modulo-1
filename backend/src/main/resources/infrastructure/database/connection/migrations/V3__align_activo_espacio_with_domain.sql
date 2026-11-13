-- Alinea el esquema con el dominio de develop:
--  * Activo exige un tipo de activo (tipo_activo_id).
--  * Espacio exige un codigo de negocio unico.
--  * TipoActivo expone el plazo de devolucion.

ALTER TABLE tipo_activo
    ADD COLUMN IF NOT EXISTS plazo_devolucion INTEGER NOT NULL DEFAULT 0
        CHECK (plazo_devolucion >= 0);

ALTER TABLE espacio
    ADD COLUMN IF NOT EXISTS codigo VARCHAR(100);

UPDATE espacio
SET codigo = 'ESP-' || REPLACE(id::text, '-', '')
WHERE codigo IS NULL;

ALTER TABLE espacio
    ALTER COLUMN codigo SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_espacio_codigo ON espacio (codigo);

ALTER TABLE activo
    ADD COLUMN IF NOT EXISTS tipo_activo_id UUID;

INSERT INTO tipo_activo (nombre, descripcion, plazo_devolucion)
SELECT 'GENERAL', 'Tipo de activo por defecto', 0
WHERE NOT EXISTS (SELECT 1 FROM tipo_activo WHERE nombre = 'GENERAL');

UPDATE activo
SET tipo_activo_id = (SELECT id FROM tipo_activo WHERE nombre = 'GENERAL' LIMIT 1)
WHERE tipo_activo_id IS NULL;

ALTER TABLE activo
    ALTER COLUMN tipo_activo_id SET NOT NULL;

ALTER TABLE activo
    ADD CONSTRAINT fk_activo_tipo_activo
        FOREIGN KEY (tipo_activo_id) REFERENCES tipo_activo (id);

CREATE INDEX IF NOT EXISTS idx_activo_tipo_activo ON activo (tipo_activo_id);
