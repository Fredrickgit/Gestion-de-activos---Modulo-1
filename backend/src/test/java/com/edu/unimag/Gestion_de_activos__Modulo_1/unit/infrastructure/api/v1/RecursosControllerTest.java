package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.ActualizarEstadoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.DisponibilidadView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.EstadoActualizadoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.PaginaView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoFiltro;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ActualizarEstadoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.DisponibilidadResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.EstadoActualizadoResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.PageResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1.RecursosController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursosControllerTest {

    @Mock
    private GestionRecursos gestionRecursos;

    private RecursosController controller;

    private UUID id;

    @BeforeEach
    void setUp() {
        controller = new RecursosController(gestionRecursos, new RecursoApiMapper());
        id = UUID.randomUUID();
    }

    @Test
    @DisplayName("Lista recursos paginados")
    void listarDelegaYPagina() {
        RecursoResumenView resumen = new RecursoResumenView(
                id, "ACT-001", "Microscopio #5", "ACTIVO", "DISPONIBLE", "OPTIMO", "Facultad", "Ubicacion", 30, 30, null);
        when(gestionRecursos.listar(any(RecursoFiltro.class), eq(0), eq(20)))
                .thenReturn(new PaginaView<>(List.of(resumen), 0, 20, 1));

        PageResponse<RecursoResumenResponse> response = controller.listar(null, null, null, 0, 20);

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(1, response.totalElements());
        assertEquals("ACT-001", response.content().get(0).codigo());
    }

    @Test
    @DisplayName("Obtiene el detalle de un recurso")
    void obtenerRetornaDetalle() {
        RecursoDetalleView view = new RecursoDetalleView(
                id, "ACT-001", "Microscopio #5", "ACTIVO", "OPTIMO", "DISPONIBLE", "Ubicacion", "Facultad", 30, 30, List.of());
        when(gestionRecursos.obtener(id)).thenReturn(view);

        RecursoDetalleResponse response = controller.obtener(id);

        assertNotNull(response);
        assertEquals("ACT-001", response.codigo());
    }

    @Test
    @DisplayName("Retorna la disponibilidad del recurso")
    void disponibilidadRetornaDisponibilidad() {
        DisponibilidadView view = new DisponibilidadView(id, "DISPONIBLE", true, null, null);
        when(gestionRecursos.disponibilidad(id, null)).thenReturn(view);

        DisponibilidadResponse response = controller.disponibilidad(id);

        assertNotNull(response);
        assertEquals("DISPONIBLE", response.estado());
        assertEquals(true, response.disponible());
    }

    @Test
    @DisplayName("Actualiza el estado del recurso")
    void actualizarEstadoDelegaYRetornaRespuesta() {
        ActualizarEstadoRequest request = new ActualizarEstadoRequest(
                EstadoDisponibilidadRecurso.DISPONIBLE, "Cambio manual", "Usuario");
        EstadoActualizadoView view = new EstadoActualizadoView(
                id, "ACTIVO", "EN_USO", "DISPONIBLE", "Usuario", OffsetDateTime.now());

        when(gestionRecursos.actualizarEstado(eq(id), any(ActualizarEstadoCommand.class), eq("corr-1")))
                .thenReturn(view);

        EstadoActualizadoResponse response = controller.actualizarEstado(id, request, "corr-1");

        assertNotNull(response);
        assertEquals("DISPONIBLE", response.estadoNuevo());
        verify(gestionRecursos).actualizarEstado(eq(id), any(ActualizarEstadoCommand.class), eq("corr-1"));
    }
}
