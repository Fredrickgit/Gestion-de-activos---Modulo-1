package com.edu.unimag.Gestion_de_activos__Modulo_1.mock.applications;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoInput;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioInput;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.CrearActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.CrearEspacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCapacityException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
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

/**
 * Tests for CreacionRecurso use case.
 * Verifies the creation of activos and espacios with proper mocking of repositories.
 */
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
    private CrearActivo crearActivo;

    @InjectMocks
    private CrearEspacio crearEspacio;

    private UUID estadoDisponibleId;
    private UUID espacioId;
    private UUID estadoFisicoId;
    private UUID tipoActivoId;
    private UUID facultadId;
    private UUID ubicacionId;

    @BeforeEach
    void setUp() {
        estadoDisponibleId = UUID.randomUUID();
        espacioId = UUID.randomUUID();
        estadoFisicoId = UUID.randomUUID();
        tipoActivoId = UUID.randomUUID();
        facultadId = UUID.randomUUID();
        ubicacionId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Crear activo exitoso en memoria")
    void crearActivoExitoso() {
        CrearActivoInput input = new CrearActivoInput(
                "Microscopio #5",
                "1001",
                espacioId,
                estadoFisicoId,
                tipoActivoId);

        when(activoRepository.existsByCodigo("1001")).thenReturn(false);
        when(espacioRepository.existsById(espacioId)).thenReturn(true);
        when(estadoActivoRepository.existsById(estadoFisicoId)).thenReturn(true);
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        var creado = crearActivo.ejecutar(input);

        assertNotNull(creado);
        assertEquals("Microscopio #5", creado.nombre());
        assertEquals("1001", creado.codigo());
        verify(activoRepository, times(1)).save(any(Activo.class));
    }

    @Test
    @DisplayName("Lanzar excepción si el código de activo ya está registrado")
    void crearActivoCodigoDuplicado() {
        CrearActivoInput input = new CrearActivoInput(
                "Computador Dell",
                "1001",
                espacioId,
                estadoFisicoId,
                tipoActivoId);

        when(activoRepository.existsByCodigo("1001")).thenReturn(true);

        DomainException ex = assertThrows(DomainException.class,
                () -> crearActivo.ejecutar(input));

        assertEquals("La placa de inventario/código ya se encuentra registrada", ex.getMessage());
        verify(activoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear espacio exitoso en memoria")
    void crearEspacioExitoso() {
        CrearEspacioInput input = new CrearEspacioInput(
                "Lab Telecomunicaciones",
                "ESP-010",
                30,
                facultadId,
                ubicacionId,
                12);

        when(espacioRepository.existsByNombre("Lab Telecomunicaciones")).thenReturn(false);
        when(facultadRepository.existsById(facultadId)).thenReturn(true);
        when(ubicacionUniversitariaRepository.existsById(ubicacionId)).thenReturn(true);
        when(espacioRepository.save(any(Espacio.class))).thenAnswer(i -> i.getArgument(0));

        var creado = crearEspacio.ejecutar(input);

        assertNotNull(creado);
        assertEquals("Lab Telecomunicaciones", creado.nombre());
        assertEquals("ESP-010", creado.codigo());
        verify(espacioRepository, times(1)).save(any(Espacio.class));
    }

    @Test
    @DisplayName("Rechazar creación de espacio con aforo negativo o cero")
    void crearEspacioAforoInvalido() {
        CrearEspacioInput input = new CrearEspacioInput(
                "Lab Robótica",
                "ESP-011",
                0,
                facultadId,
                ubicacionId,
                12);

        InvalidCapacityException ex = assertThrows(InvalidCapacityException.class,
                () -> crearEspacio.ejecutar(input));

        verify(espacioRepository, never()).save(any());
    }
}
