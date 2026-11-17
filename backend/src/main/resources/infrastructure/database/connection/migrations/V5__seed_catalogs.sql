-- Catálogos mínimos para que una base nueva en Render pueda operar.
-- Sin estos registros el formulario de registro no puede resolver los UUID
-- que exige la API (facultad, ubicación y tipo de activo).

INSERT INTO facultad (nombre, descripcion)
SELECT nombre, 'Facultad de la Universidad del Magdalena'
FROM (VALUES
    ('Ingeniería'),
    ('Ciencias Básicas'),
    ('Humanidades'),
    ('Ciencias de la Salud'),
    ('Ciencias Empresariales'),
    ('General')
) AS valores(nombre)
WHERE NOT EXISTS (SELECT 1 FROM facultad f WHERE f.nombre = valores.nombre);

INSERT INTO ubicacion (nombre, descripcion)
SELECT nombre, 'Ubicación física registrada'
FROM (VALUES
    ('Bloque Ciencias, Piso 1'),
    ('Bloque Sierra Nevada, Piso 2'),
    ('Biblioteca Central Germán Bula'),
    ('Auditorio Roque Morelli'),
    ('Bloque de Ingenierías, Terraza'),
    ('Edificio de aulas Mar Caribe'),
    ('Hangar A'),
    ('Bloque 2')
) AS valores(nombre)
WHERE NOT EXISTS (SELECT 1 FROM ubicacion u WHERE u.nombre = valores.nombre);

INSERT INTO tipo_activo (nombre, descripcion, plazo_devolucion)
SELECT nombre, 'Tipo de activo registrado', plazo
FROM (VALUES
    ('GENERAL', 0),
    ('Cómputo Alto Rendimiento', 1),
    ('Cómputo Universitario', 2),
    ('Audiovisual Producción', 5),
    ('Multimedia', 3),
    ('Equipo Técnico', 1),
    ('Equipo de Laboratorio', 2),
    ('Laboratorio Clínico', 1),
    ('Mobiliario Universitario', 2)
) AS valores(nombre, plazo)
WHERE NOT EXISTS (SELECT 1 FROM tipo_activo t WHERE t.nombre = valores.nombre);

INSERT INTO estado_disponibilidad (disponibilidad, descripcion)
SELECT valor, valor
FROM (VALUES
    ('DISPONIBLE'),
    ('EN_USO'),
    ('EN_MANTENIMIENTO'),
    ('RESERVADO'),
    ('BLOQUEO_ACADEMICO')
) AS valores(valor)
ON CONFLICT (disponibilidad) DO NOTHING;

INSERT INTO estado_activo (estado, descripcion)
SELECT valor, valor
FROM (VALUES
    ('NUEVO'),
    ('OPTIMO'),
    ('MAL_ESTADO'),
    ('AVERIADO')
) AS valores(valor)
ON CONFLICT (estado) DO NOTHING;
