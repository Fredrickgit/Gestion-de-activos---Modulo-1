DO $$
DECLARE
    recursos_legacy text;
BEGIN
    SELECT string_agg(
        format('%s:%s=%s', tipo_recurso, recurso_id, disponibilidad),
        '; ' ORDER BY tipo_recurso, recurso_id
    )
    INTO recursos_legacy
    FROM (
        SELECT 'ACTIVO' AS tipo_recurso, activo.id::text AS recurso_id, estado.disponibilidad
        FROM activo
        JOIN estado_disponibilidad estado ON estado.id = activo.disponibilidad_id
        WHERE estado.disponibilidad IN ('RESERVADO', 'BLOQUEO_ACADEMICO')
        UNION ALL
        SELECT 'ESPACIO' AS tipo_recurso, espacio.id::text AS recurso_id, estado.disponibilidad
        FROM espacio
        JOIN estado_disponibilidad estado ON estado.id = espacio.disponibilidad_id
        WHERE estado.disponibilidad IN ('RESERVADO', 'BLOQUEO_ACADEMICO')
    ) recursos;

    IF recursos_legacy IS NOT NULL THEN
        RAISE EXCEPTION
            'No se pueden retirar estados de reserva automáticamente. Sincronice estas ocupaciones con M2 y luego actualice M1 a DISPONIBLE: %',
            recursos_legacy;
    END IF;
END $$;

DELETE FROM estado_disponibilidad
WHERE disponibilidad IN ('RESERVADO', 'BLOQUEO_ACADEMICO');

ALTER TABLE estado_disponibilidad
    ADD CONSTRAINT chk_estado_disponibilidad_operativo
    CHECK (disponibilidad IN ('DISPONIBLE', 'EN_USO', 'EN_MANTENIMIENTO'));
