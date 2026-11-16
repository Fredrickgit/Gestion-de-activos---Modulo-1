package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearEspacioRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1.EspaciosController;
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
class EspaciosControllerTest {

    @Mock
    private GestionRecursos gestionRecursos;

    private EspaciosController controller;

    @BeforeEach
    void setUp() {
        controller = new EspaciosController(gestionRecursos, new RecursoApiMapper());
    }

    @Test
    @DisplayName("Delega la creación de un espacio en el caso de uso y devuelve el detalle")
    void crearDelegaYRetornaDetalle() {
        UUID id = UUID.randomUUID();
        when(gestionRecursos.crearEspacio(any(CrearEspacioCommand.class))).thenReturn(
                new com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView(
                        id, "ESP-010", "Lab Telecomunicaciones", "ESPACIO", "DISPONIBLE", "OPTIMO",
                        "Facultad", "Ubicacion", 30, 30, List.of()));

        RecursoDetalleResponse response = controller.crear(new CrearEspacioRequest(
                "Lab Telecomunicaciones", "ESP-010", 30, UUID.randomUUID(), UUID.randomUUID(), null,
                List.of(), 30));

        assertNotNull(response);
        assertEquals("ESP-010", response.codigo());
        assertEquals("Lab Telecomunicaciones", response.nombre());
        verify(gestionRecursos).crearEspacio(any(CrearEspacioCommand.class));
    }
}
