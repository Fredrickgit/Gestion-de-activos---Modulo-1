package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.schemas;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.SolicitudInvalidaException;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validación y normalización de los parámetros de consulta del catálogo.
 */
public final class RecursoSchemas {

    private static final Pattern UUID_CANONICO = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 20;

    private RecursoSchemas() {
        // Utility class
    }

    public static String parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }
        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.equals("ACTIVO") && !normalizado.equals("ESPACIO")) {
            throw new SolicitudInvalidaException(
                    "El parámetro 'tipo' debe ser 'activo' o 'espacio'.");
        }
        return normalizado;
    }

    public static String parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        String normalizado = estado.trim().toUpperCase(Locale.ROOT);
        try {
            return EstadoDisponibilidadRecurso.valueOf(normalizado).name();
        } catch (IllegalArgumentException ex) {
            throw new SolicitudInvalidaException(
                    "El parámetro 'estado' no es válido. Valores permitidos: DISPONIBLE, EN_USO, "
                            + "EN_MANTENIMIENTO, RESERVADO, BLOQUEO_ACADEMICO.");
        }
    }

    public static FacultadCriteria parseFacultad(String facultad) {
        if (facultad == null || facultad.isBlank()) {
            return new FacultadCriteria(null, null);
        }
        String valor = facultad.trim();
        if (UUID_CANONICO.matcher(valor).matches()) {
            return new FacultadCriteria(UUID.fromString(valor), null);
        }
        return new FacultadCriteria(null, valor);
    }

    public record FacultadCriteria(UUID id, String nombre) {
    }
}
