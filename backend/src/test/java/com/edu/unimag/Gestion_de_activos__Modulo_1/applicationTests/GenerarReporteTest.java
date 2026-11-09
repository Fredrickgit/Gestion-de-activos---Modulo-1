package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.GenerarReporte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerarReporteTest {

    @Mock
    private ActivoRepository activoRepository;
    @Mock
    private EspacioRepository espacioRepository;

    @InjectMocks
    private GenerarReporte generarReporte;

    @Test
    @DisplayName("Generar previsualización de reporte con ordenamiento predeterminado")
    void previsualizarReporteActivosExitoso() {
        Activo activo = Activo.builder().id(UUID.randomUUID()).nombre("Laptop Dell").build();
        Page<Activo> page = new PageImpl<>(List.of(activo));

        when(activoRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Activo> resultado = generarReporte.previsualizarReporteActivos(0, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(activoRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Lanzar excepción al navegar a una página sin registros")
    void previsualizarReportePaginaFueraDeRango() {
        when(activoRepository.findAll(any(PageRequest.class))).thenReturn(Page.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generarReporte.previsualizarReporteActivos(3, "nombre"));

        assertEquals("No hay más registros", ex.getMessage());
    }

    @Test
    @DisplayName("Lanzar excepción cuando el reporte filtrado por estado no arroje datos")
    void previsualizarReportePorEstadoSinResultados() {
        UUID estadoId = UUID.randomUUID();
        when(activoRepository.findByDisponibilidadId(estadoId)).thenReturn(Collections.emptyList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generarReporte.previsualizarReporteActivosPorEstado(estadoId, 0));

        assertEquals("No hay recursos que coincidan con los filtros", ex.getMessage());
    }
}