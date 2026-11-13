package com.edu.unimag.Gestion_de_activos__Modulo_1.mock.applications;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.ConsultaInventarios;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ConsultaInventariosTest {

    @Mock
    private EspacioRepository espacioRepository;

    @Mock
    private ActivoRepository activoRepository;

    @InjectMocks
    private ConsultaInventarios consultaInventarios;

    @Test
    @DisplayName("Buscar espacios por nombre exitosamente")
    void buscarEspaciosPorNombreExitoso() {
        String query = "Lab";
        Espacio espacio = Espacio.builder()
                .id(UUID.randomUUID())
                .nombre("Lab Telecomunicaciones")
                .codigo("ESP-001")
                .aforo(30)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(24)
                .facultadId(UUID.randomUUID())
                .disponibilidadId(UUID.randomUUID())
                .ubicacionId(UUID.randomUUID())
                .build();

        when(espacioRepository.findByNombreContainingIgnoreCase(query)).thenReturn(List.of(espacio));

        var resultado = consultaInventarios.buscarEspaciosPorNombre(query, 0);

        assertNotNull(resultado);
        verify(espacioRepository, times(1)).findByNombreContainingIgnoreCase(query);
    }

    @Test
    @DisplayName("Validar que el parámetro de búsqueda no esté vacío")
    void buscarEspaciosPorNombreVacio() {
        DomainException ex = assertThrows(DomainException.class,
                () -> consultaInventarios.buscarEspaciosPorNombre("   ", 0));

        assertEquals("El criterio de búsqueda no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Lanzar excepción cuando la consulta en inventario no tiene coincidencias")
    void buscarEspaciosSinResultados() {
        when(espacioRepository.findByNombreContainingIgnoreCase("Inexistente")).thenReturn(Collections.emptyList());

        DomainException ex = assertThrows(DomainException.class,
                () -> consultaInventarios.buscarEspaciosPorNombre("Inexistente", 0));

        assertEquals("No se encontraron resultados para los criterios de búsqueda", ex.getMessage());
    }
}
