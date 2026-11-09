package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.*;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.CreacionRecurso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreacionRecursoTest {

    @Mock
    private ActivoRepository activoRepository;
    @Mock
    private EspacioRepository espacioRepository;
    @Mock
    private EstadoActivoRepository estadoActivoRepository;
    @Mock
    private FacultadRepository facultadRepository;
    @Mock
    private UbicacionUniversitariaRepository ubicacionUniversitariaRepository;

    @InjectMocks
    private CreacionRecurso creacionRecurso;

    private UUID estadoDisponibleId;
    private UUID espacioId;
    private UUID estadoFisicoId;
    private UUID facultadId;
    private UUID ubicacionId;

    @BeforeEach
    void setUp() {
        estadoDisponibleId = UUID.randomUUID();
        espacioId = UUID.randomUUID();
        estadoFisicoId = UUID.randomUUID();
        facultadId = UUID.randomUUID();
        ubicacionId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Crear activo exitoso en memoria")
    void crearActivoExitoso() {
        Activo activo = Activo.builder()
                .nombre("Microscopio #5")
                .codigo(1001)
                .espacioId(espacioId)
                .estadoFisicoId(estadoFisicoId)
                .build();

        when(activoRepository.existsByCodigo("1001")).thenReturn(false);
        when(espacioRepository.existsById(espacioId)).thenReturn(true);
        when(estadoActivoRepository.existsById(estadoFisicoId)).thenReturn(true);
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        Activo creado = creacionRecurso.crearActivo(activo, estadoDisponibleId);

        assertNotNull(creado);
        assertEquals(CategoriaRecurso.ACTIVO, creado.getCategoria());
        assertEquals(estadoDisponibleId, creado.getDisponibilidadId());
        verify(activoRepository, times(1)).save(activo);
    }

    @Test
    @DisplayName("Lanzar excepción si el código de activo ya está registrado")
    void crearActivoCodigoDuplicado() {
        Activo activo = Activo.builder()
                .nombre("Computador Dell")
                .codigo(1001)
                .espacioId(espacioId)
                .estadoFisicoId(estadoFisicoId)
                .build();

        when(activoRepository.existsByCodigo("1001")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> creacionRecurso.crearActivo(activo, estadoDisponibleId));

        assertEquals("La placa de inventario/código ya se encuentra registrada", ex.getMessage());
        verify(activoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear espacio exitoso en memoria")
    void crearEspacioExitoso() {
        Espacio espacio = Espacio.builder()
                .nombre("Lab Telecomunicaciones")
                .aforo(30)
                .facultadId(facultadId)
                .ubicacionId(ubicacionId)
                .build();

        when(espacioRepository.existsByNombre("Lab Telecomunicaciones")).thenReturn(false);
        when(facultadRepository.existsById(facultadId)).thenReturn(true);
        when(ubicacionUniversitariaRepository.existsById(ubicacionId)).thenReturn(true);
        when(espacioRepository.save(any(Espacio.class))).thenAnswer(i -> i.getArgument(0));

        Espacio creado = creacionRecurso.crearEspacio(espacio, estadoDisponibleId);

        assertNotNull(creado);
        assertEquals(CategoriaRecurso.ESPACIO, creado.getCategoria());
        assertEquals(estadoDisponibleId, creado.getDisponibilidadId());
        verify(espacioRepository, times(1)).save(espacio);
    }

    @Test
    @DisplayName("Rechazar creación de espacio con aforo negativo o cero")
    void crearEspacioAforoInvalido() {
        Espacio espacio = Espacio.builder()
                .nombre("Lab Robótica")
                .aforo(0)
                .facultadId(facultadId)
                .ubicacionId(ubicacionId)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> creacionRecurso.crearEspacio(espacio, estadoDisponibleId));

        assertEquals("El aforo máximo debe ser un número entero positivo mayor a cero", ex.getMessage());
        verify(espacioRepository, never()).save(any());
    }
}