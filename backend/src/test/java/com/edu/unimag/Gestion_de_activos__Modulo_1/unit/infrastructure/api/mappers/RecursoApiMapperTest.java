package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.*;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.DisponibilidadResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.EstadoActualizadoResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.HorarioOcupacionResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.PageResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecursoApiMapperTest {

    private RecursoApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RecursoApiMapper();
    }

    @Test
    @DisplayName("Traduce el detalle de aplicación al contrato HTTP")
    void toDetalle() {
        UUID id = UUID.randomUUID();
        RecursoDetalleView view = new RecursoDetalleView(
                id, "ACT-001", "Microscopio #5", "ACTIVO", "OPTIMO", "DISPONIBLE", "Lab", "Facultad", 30, 10, List.of());

        RecursoDetalleResponse response = mapper.toDetalle(view);

        assertEquals(id, response.id());
        assertEquals("ACT-001", response.codigo());
        assertEquals("Microscopio #5", response.nombre());
        assertEquals("ACTIVO", response.tipo());
        assertEquals("OPTIMO", response.estadoFisico());
        assertEquals("DISPONIBLE", response.estado());
        assertEquals(30, response.plazoDevolucion());
        assertEquals(10, response.aforoMaximo());
        assertEquals(List.of(), response.equipamiento());
    }

    @Test
    @DisplayName("Traduce la página de recursos al contrato HTTP")
    void toPage() {
        UUID id = UUID.randomUUID();
        RecursoResumenView resumen = new RecursoResumenView(
                id, "ACT-001", "Microscopio #5", "ACTIVO", "DISPONIBLE", "OPTIMO", "Facultad", "Lab", 10, 30, null);
        PaginaView<RecursoResumenView> pagina = new PaginaView<>(List.of(resumen), 2, 20, 45);

        PageResponse<RecursoResumenResponse> response = mapper.toPage(pagina);

        assertEquals(2, response.page());
        assertEquals(20, response.size());
        assertEquals(45, response.totalElements());
        assertEquals(1, response.content().size());
        assertEquals("ACT-001", response.content().get(0).codigo());
    }

    @Test
    @DisplayName("Traduce la disponibilidad sin horario asignado")
    void toDisponibilidadSinHorario() {
        DisponibilidadView view = new DisponibilidadView(UUID.randomUUID(), "DISPONIBLE", true, null, null);

        DisponibilidadResponse response = mapper.toDisponibilidad(view);

        assertEquals("DISPONIBLE", response.estado());
        assertEquals(true, response.disponible());
        assertNull(response.horarioAsignado());
        assertNull(response.motivoNoDisponibilidad());
    }

    @Test
    @DisplayName("Traduce la disponibilidad con horario asignado")
    void toDisponibilidadConHorario() {
        DisponibilidadView view = new DisponibilidadView(
                UUID.randomUUID(), "EN_USO", false,
                new HorarioOcupacionView(OffsetDateTime.now(), OffsetDateTime.now()), "En uso");

        DisponibilidadResponse response = mapper.toDisponibilidad(view);

        assertEquals("EN_USO", response.estado());
        assertEquals(false, response.disponible());
        assertNotNull(response.horarioAsignado());
    }

    @Test
    @DisplayName("Traduce la actualización de estado al contrato HTTP")
    void toEstadoActualizado() {
        EstadoActualizadoView view = new EstadoActualizadoView(
                UUID.randomUUID(), "ACTIVO", "EN_USO", "DISPONIBLE", "Usuario", OffsetDateTime.now());

        EstadoActualizadoResponse response = mapper.toEstadoActualizado(view);

        assertEquals(view.id().toString(), response.id());
        assertEquals("ACTIVO", response.tipo());
        assertEquals("EN_USO", response.estadoAnterior());
        assertEquals("DISPONIBLE", response.estadoNuevo());
        assertEquals("Usuario", response.auditoria().usuario());
    }
}
