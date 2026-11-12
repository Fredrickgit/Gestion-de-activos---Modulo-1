package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.repository;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de lectura (CQRS ligero) que proyecta activos y espacios en un
 * listado unificado de recursos, tal como lo consume GET /api/v1/recursos.
 */
@Repository
public class RecursoReadRepository {

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
                   NULL::jsonb AS equipamiento
            FROM activo a
            LEFT JOIN espacio e ON e.id = a.espacio_id
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = a.disponibilidad_id
            LEFT JOIN estado_activo ea ON ea.id = a.estado_fisico_id
            WHERE (CAST(:tipo AS text) IS NULL OR CAST(:tipo AS text) = 'ACTIVO')
              AND (CAST(:estado AS text) IS NULL OR ed.disponibilidad = CAST(:estado AS text))
              AND (CAST(:facultadId AS uuid) IS NULL OR f.id = CAST(:facultadId AS uuid))
              AND (CAST(:facultadNombre AS text) IS NULL
                   OR LOWER(f.nombre) LIKE '%' || LOWER(CAST(:facultadNombre AS text)) || '%')
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
                   e.equipamiento AS equipamiento
            FROM espacio e
            LEFT JOIN facultad f ON f.id = e.facultad_id
            LEFT JOIN ubicacion u ON u.id = e.ubicacion_id
            LEFT JOIN estado_disponibilidad ed ON ed.id = e.disponibilidad_id
            WHERE (CAST(:tipo AS text) IS NULL OR CAST(:tipo AS text) = 'ESPACIO')
              AND (CAST(:estado AS text) IS NULL OR ed.disponibilidad = CAST(:estado AS text))
              AND (CAST(:facultadId AS uuid) IS NULL OR f.id = CAST(:facultadId AS uuid))
              AND (CAST(:facultadNombre AS text) IS NULL
                   OR LOWER(f.nombre) LIKE '%' || LOWER(CAST(:facultadNombre AS text)) || '%')
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
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public RecursoReadRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<RecursoResumenResponse> listar(String tipo,
                                               String estado,
                                               UUID facultadId,
                                               String facultadNombre,
                                               int page,
                                               int size) {
        String sql = "SELECT * FROM (" + UNION_RECURSOS + ") recursos "
                + "ORDER BY nombre ASC NULLS LAST, id ASC LIMIT :limit OFFSET :offset";

        MapSqlParameterSource params = parametros(tipo, estado, facultadId, facultadNombre)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);

        return jdbcTemplate.query(sql, params, resumenRowMapper());
    }

    public long contar(String tipo,
                       String estado,
                       UUID facultadId,
                       String facultadNombre) {
        String sql = "SELECT COUNT(*) FROM (" + UNION_RECURSOS + ") recursos";
        Long total = jdbcTemplate.queryForObject(
                sql,
                parametros(tipo, estado, facultadId, facultadNombre),
                Long.class);
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

    private MapSqlParameterSource parametros(String tipo,
                                             String estado,
                                             UUID facultadId,
                                             String facultadNombre) {
        return new MapSqlParameterSource()
                .addValue("tipo", tipo)
                .addValue("estado", estado)
                .addValue("facultadId", facultadId)
                .addValue("facultadNombre", escaparLike(facultadNombre));
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
                getInteger(rs, "plazo_devolucion"));
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
