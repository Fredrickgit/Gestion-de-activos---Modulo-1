package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.repository;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoFiltro;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de lectura (CQRS ligero) que proyecta activos y espacios en un
 * listado unificado de recursos, tal como lo consume GET /api/v1/recursos.
 */
@Repository
public class RecursoReadRepository {

    private static final Map<String, String> COLUMNAS_ORDEN = Map.of(
            "nombre", "nombre",
            "tipo", "tipo",
            "estado", "estado",
            "codigo", "codigo",
            "fecharegistro", "fecha_registro",
            "aforo", "aforo_maximo");

    private static final String UNION_RECURSOS = """
            SELECT 'ACTIVO'::text AS tipo,
                   a.id AS id,
                   a.codigo AS codigo,
                   a.nombre AS nombre,
                   ed.disponibilidad::text AS estado,
                   ea.estado::text AS estado_fisico,
                   f.nombre AS facultad,
                   u.nombre AS ubicacion,
                   a.plazo_devolucion AS plazo_devolucion,
                   NULL::integer AS aforo_maximo,
                   NULL::jsonb AS equipamiento,
                   a.fecha_registro AS fecha_registro
            FROM activo a
            LEFT JOIN espacio e ON e.id = a.espacio_id
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = a.disponibilidad_id
            LEFT JOIN estado_activo ea ON ea.id = a.estado_fisico_id
            WHERE a.dado_de_baja = FALSE
              AND (CAST(:tipo AS text) IS NULL OR CAST(:tipo AS text) = 'ACTIVO')
              AND (CAST(:estado AS text) IS NULL OR ed.disponibilidad = CAST(:estado AS text))
              AND (CAST(:facultadId AS uuid) IS NULL OR f.id = CAST(:facultadId AS uuid))
              AND (CAST(:facultadNombre AS text) IS NULL
                   OR LOWER(f.nombre) LIKE '%' || LOWER(CAST(:facultadNombre AS text)) || '%')
              AND (CAST(:nombre AS text) IS NULL
                   OR LOWER(a.nombre) LIKE '%' || LOWER(CAST(:nombre AS text)) || '%')
              -- Los activos no tienen aforo: si se filtra por aforo, no aplican.
              AND CAST(:aforoMinimo AS integer) IS NULL
            UNION ALL
            SELECT 'ESPACIO'::text AS tipo,
                   e.id AS id,
                   e.codigo AS codigo,
                   e.nombre AS nombre,
                   ed.disponibilidad::text AS estado,
                   NULL::varchar AS estado_fisico,
                   f.nombre AS facultad,
                   u.nombre AS ubicacion,
                   e.plazo_devolucion AS plazo_devolucion,
                   e.aforo_maximo AS aforo_maximo,
                   e.equipamiento AS equipamiento,
                   e.fecha_registro AS fecha_registro
            FROM espacio e
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = e.disponibilidad_id
            WHERE e.dado_de_baja = FALSE
              AND (CAST(:tipo AS text) IS NULL OR CAST(:tipo AS text) = 'ESPACIO')
              AND (CAST(:estado AS text) IS NULL OR ed.disponibilidad = CAST(:estado AS text))
              AND (CAST(:facultadId AS uuid) IS NULL OR f.id = CAST(:facultadId AS uuid))
              AND (CAST(:facultadNombre AS text) IS NULL
                   OR LOWER(f.nombre) LIKE '%' || LOWER(CAST(:facultadNombre AS text)) || '%')
              AND (CAST(:nombre AS text) IS NULL
                   OR LOWER(e.nombre) LIKE '%' || LOWER(CAST(:nombre AS text)) || '%')
              AND (CAST(:aforoMinimo AS integer) IS NULL
                   OR e.aforo_maximo >= CAST(:aforoMinimo AS integer))
            """;

    private static final String DETALLE_ACTIVO = """
            SELECT 'ACTIVO'::text AS tipo,
                   a.id AS id,
                   a.codigo AS codigo,
                   a.nombre AS nombre,
                   ed.disponibilidad::text AS estado,
                   ea.estado::text AS estado_fisico,
                   f.nombre AS facultad,
                   u.nombre AS ubicacion,
                   a.plazo_devolucion AS plazo_devolucion,
                   NULL::integer AS aforo_maximo,
                   NULL::jsonb AS equipamiento
            FROM activo a
            LEFT JOIN espacio e ON e.id = a.espacio_id
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = a.disponibilidad_id
            LEFT JOIN estado_activo ea ON ea.id = a.estado_fisico_id
            WHERE a.id = :id
              AND a.dado_de_baja = FALSE
            """;

    private static final String DETALLE_ESPACIO = """
            SELECT 'ESPACIO'::text AS tipo,
                   e.id AS id,
                   e.codigo AS codigo,
                   e.nombre AS nombre,
                   ed.disponibilidad::text AS estado,
                   NULL::varchar AS estado_fisico,
                   f.nombre AS facultad,
                   u.nombre AS ubicacion,
                   e.plazo_devolucion AS plazo_devolucion,
                   e.aforo_maximo AS aforo_maximo,
                   e.equipamiento AS equipamiento
            FROM espacio e
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = e.disponibilidad_id
            WHERE e.id = :id
              AND e.dado_de_baja = FALSE
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public RecursoReadRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<RecursoResumenResponse> listar(RecursoFiltro filtro,
                                               String orden,
                                               String direccion,
                                               int page,
                                               int size) {
        String columna = COLUMNAS_ORDEN.getOrDefault(
                orden == null ? "nombre" : orden.toLowerCase(Locale.ROOT), "nombre");
        String sentido = "DESC".equalsIgnoreCase(direccion) ? "DESC" : "ASC";
        String sql = "SELECT * FROM (" + UNION_RECURSOS + ") recursos "
                + "ORDER BY " + columna + " " + sentido + " NULLS LAST, id ASC "
                + "LIMIT :limit OFFSET :offset";

        MapSqlParameterSource params = parametros(filtro)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);

        return jdbcTemplate.query(sql, params, resumenRowMapper());
    }

    public long contar(RecursoFiltro filtro) {
        String sql = "SELECT COUNT(*) FROM (" + UNION_RECURSOS + ") recursos";
        Long total = jdbcTemplate.queryForObject(sql, parametros(filtro), Long.class);
        return total != null ? total : 0L;
    }

    public Optional<RecursoDetalleResponse> findDetalle(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        List<RecursoDetalleResponse> activos = jdbcTemplate.query(
                DETALLE_ACTIVO, params, detalleRowMapper());
        if (!activos.isEmpty()) {
            return Optional.of(activos.get(0));
        }

        List<RecursoDetalleResponse> espacios = jdbcTemplate.query(
                DETALLE_ESPACIO, params, detalleRowMapper());
        return espacios.stream().findFirst();
    }

    private MapSqlParameterSource parametros(RecursoFiltro filtro) {
        RecursoFiltro criterios = filtro == null ? RecursoFiltro.vacio() : filtro;
        return new MapSqlParameterSource()
                .addValue("tipo", criterios.tipo())
                .addValue("estado", criterios.estado())
                .addValue("facultadId", criterios.facultadId())
                .addValue("facultadNombre", escaparLike(criterios.facultadNombre()))
                .addValue("nombre", escaparLike(criterios.nombre()))
                .addValue("aforoMinimo", criterios.aforoMinimo());
    }

    private String escaparLike(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private RowMapper<RecursoResumenResponse> resumenRowMapper() {
        return (rs, rowNum) -> new RecursoResumenResponse(
                rs.getObject("id", UUID.class),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getString("estado"),
                rs.getString("estado_fisico"),
                rs.getString("facultad"),
                rs.getString("ubicacion"),
                getInteger(rs, "aforo_maximo"),
                getInteger(rs, "plazo_devolucion"),
                rs.getObject("fecha_registro", LocalDateTime.class));
    }

    private RowMapper<RecursoDetalleResponse> detalleRowMapper() {
        return (rs, rowNum) -> new RecursoDetalleResponse(
                rs.getObject("id", UUID.class),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("tipo"),
                rs.getString("estado_fisico"),
                rs.getString("estado"),
                rs.getString("ubicacion"),
                rs.getString("facultad"),
                getInteger(rs, "plazo_devolucion"),
                getInteger(rs, "aforo_maximo"),
                parseEquipamiento(rs.getString("equipamiento")));
    }

    private Integer getInteger(ResultSet rs, String column) throws SQLException {
        Object value = rs.getObject(column);
        return value instanceof Number number ? number.intValue() : null;
    }

    private List<UUID> parseEquipamiento(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<UUID>>() {});
        } catch (Exception ex) {
            return List.of();
        }
    }
}
