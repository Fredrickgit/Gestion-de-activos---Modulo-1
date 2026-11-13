-- Agrega las coordenadas de la ubicación universitaria, requeridas por la
-- entidad de persistencia {@code Ubicacion} y por el diccionario de datos.
ALTER TABLE ubicacion
    ADD COLUMN IF NOT EXISTS coordenadas VARCHAR(255);
