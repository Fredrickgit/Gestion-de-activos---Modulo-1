-- Migracion inicial del esquema PostgreSQL - Modulo 1 (Gestion de Activos)
-- Fuente: specs/PLAN.md, specs/REST.md, specs/KAFKA.md, documentation/resources/Diccionario.md
--
-- Modelo: herencia JOINED. La tabla "recurso" es el padre comun de
-- "activo" y "espacio". Todos los identificadores son UUID generados por
-- PostgreSQL con gen_random_uuid().

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------------------
-- Catalogos
-- ---------------------------------------------------------------------------

CREATE TABLE facultad (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE ubicacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE tipo_activo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE estado_activo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estado VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE estado_disponibilidad (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    disponibilidad VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- ---------------------------------------------------------------------------
-- Espacio y Activo (entidades independientes)
-- ---------------------------------------------------------------------------

CREATE TABLE espacio (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    categoria VARCHAR(20) NOT NULL DEFAULT 'ESPACIO' CHECK (categoria = 'ESPACIO'),
    aforo_maximo INTEGER NOT NULL CHECK (aforo_maximo > 0),
    plazo_devolucion INTEGER NOT NULL DEFAULT 0 CHECK (plazo_devolucion >= 0),
    equipamiento JSONB NOT NULL DEFAULT '[]'::jsonb,
    facultad_id UUID NOT NULL REFERENCES facultad (id),
    ubicacion_id UUID NOT NULL REFERENCES ubicacion (id),
    disponibilidad_id UUID NOT NULL REFERENCES estado_disponibilidad (id)
);

CREATE TABLE activo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (categoria = 'ACTIVO'),
    plazo_devolucion INTEGER NOT NULL DEFAULT 0 CHECK (plazo_devolucion >= 0),
    estado_fisico_id UUID NOT NULL REFERENCES estado_activo (id),
    disponibilidad_id UUID NOT NULL REFERENCES estado_disponibilidad (id),
    espacio_id UUID REFERENCES espacio (id)
);

-- ---------------------------------------------------------------------------
-- Auditoria e idempotencia (Kafka Transactional Inbox)
-- ---------------------------------------------------------------------------

CREATE TABLE historial_recurso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_recurso UUID NOT NULL,
    estado_anterior VARCHAR(50) NOT NULL,
    estado_nuevo VARCHAR(50) NOT NULL,
    usuario_responsable VARCHAR(255) NOT NULL,
    fecha_evento TIMESTAMP NOT NULL,
    motivo VARCHAR(255)
);

CREATE TABLE eventos_procesados (
    evento_id UUID PRIMARY KEY,
    tipo_evento VARCHAR(100) NOT NULL,
    recurso_id UUID,
    payload JSONB,
    resultado VARCHAR(20) NOT NULL,
    fecha_procesamiento TIMESTAMP NOT NULL
);

-- ---------------------------------------------------------------------------
-- Indices de apoyo para consultas de catalogo y trazabilidad
-- ---------------------------------------------------------------------------

CREATE INDEX idx_espacio_disponibilidad ON espacio (disponibilidad_id);
CREATE INDEX idx_espacio_facultad ON espacio (facultad_id);
CREATE INDEX idx_espacio_ubicacion ON espacio (ubicacion_id);
CREATE INDEX idx_activo_disponibilidad ON activo (disponibilidad_id);
CREATE INDEX idx_activo_estado_fisico ON activo (estado_fisico_id);
CREATE INDEX idx_activo_espacio ON activo (espacio_id);
CREATE INDEX idx_historial_recurso ON historial_recurso (id_recurso);
CREATE INDEX idx_historial_recurso_fecha ON historial_recurso (fecha_evento);
CREATE INDEX idx_eventos_procesados_recurso ON eventos_procesados (recurso_id);
