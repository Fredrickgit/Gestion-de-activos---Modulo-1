-- Outbox transaccional para eventos salientes (estado.actualizado) y soporte
-- de baja logica + fecha de registro para reportes.

ALTER TABLE activo
    ADD COLUMN IF NOT EXISTS fecha_registro TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE espacio
    ADD COLUMN IF NOT EXISTS fecha_registro TIMESTAMP NOT NULL DEFAULT now();

ALTER TABLE activo
    ADD COLUMN IF NOT EXISTS dado_de_baja BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE espacio
    ADD COLUMN IF NOT EXISTS dado_de_baja BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_activo_dado_de_baja ON activo (dado_de_baja);
CREATE INDEX IF NOT EXISTS idx_espacio_dado_de_baja ON espacio (dado_de_baja);

-- Patron Transactional Outbox: el evento se persiste en la misma transaccion
-- que el cambio de estado y un relay programado lo publica en Kafka.
CREATE TABLE IF NOT EXISTS evento_saliente (
    id UUID PRIMARY KEY,
    topic VARCHAR(120) NOT NULL,
    clave VARCHAR(120) NOT NULL,
    payload JSONB NOT NULL,
    headers JSONB NOT NULL DEFAULT '{}'::jsonb,
    creado_en TIMESTAMP NOT NULL,
    enviado_en TIMESTAMP,
    intentos INTEGER NOT NULL DEFAULT 0,
    ultimo_error VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_evento_saliente_pendiente
    ON evento_saliente (creado_en)
    WHERE enviado_en IS NULL;
