package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearActivoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1.ActivosController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivosControllerTest {

    @Mock
    private GestionRecursos gestionRecursos;

    private ActivosController controller;

    @BeforeEach
    void setUp() {
        controller = new ActivosController(gestionRecursos, new RecursoApiMapper());
    }

    @Test
    @DisplayName("Delega la creación en el caso de uso y devuelve el detalle")
    void crearDelegaYRetornaDetalle() {
        UUID id = UUID.randomUUID();
        RecursoDetalleResponse expected = new RecursoDetalleResponse(
                id, "ACT-001", "Microscopio #5", "ACTIVO", "OPTIMO", "DISPONIBLE",
                "Lab Telecomunicaciones", "Facultad", 30, 10, List.of());

        when(gestionRecursos.crearActivo(any(CrearActivoCommand.class))).thenReturn(
                new com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView(
                        id, "ACT-001", "Microscopio #5", "ACTIVO", "OPTIMO", "DISPONIBLE",
                        "Lab Telecomunicaciones", "Facultad", 30, 10, List.of()));

        RecursoDetalleResponse response = controller.crear(new CrearActivoRequest(
                "Microscopio #5", "ACT-001", EstadoFisicoActivo.OPTIMO, UUID.randomUUID(), null, UUID.randomUUID(), 30));

        assertNotNull(response);
        assertEquals("ACT-001", response.codigo());
        assertEquals("Microscopio #5", response.nombre());
        verify(gestionRecursos).crearActivo(any(CrearActivoCommand.class));
    }
}
