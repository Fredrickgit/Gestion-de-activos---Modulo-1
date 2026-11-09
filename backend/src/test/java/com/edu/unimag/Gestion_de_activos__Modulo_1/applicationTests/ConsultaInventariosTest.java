package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.ConsultaInventarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        Espacio espacio = Espacio.builder().nombre("Lab Telecomunicaciones").build();
        Page<Espacio> page = new PageImpl<>(List.of(espacio));

        when(espacioRepository.findByNombreContainingIgnoreCase(eq(query), any(PageRequest.class)))
                .thenReturn(page);

        Page<Espacio> resultado = consultaInventarios.buscarEspaciosPorNombre(query, 0);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Validar que el parámetro de búsqueda no esté vacío")
    void buscarEspaciosPorNombreVacio() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultaInventarios.buscarEspaciosPorNombre("   ", 0));

        assertEquals("El criterio de búsqueda no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Lanzar excepción cuando la consulta en inventario no tiene coincidencias")
    void buscarEspaciosSinResultados() {
        when(espacioRepository.findByNombreContainingIgnoreCase(eq("Inexistente"), any(PageRequest.class)))
                .thenReturn(Page.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultaInventarios.buscarEspaciosPorNombre("Inexistente", 0));

        assertEquals("No se encontraron resultados para los criterios de búsqueda", ex.getMessage());
    }
}