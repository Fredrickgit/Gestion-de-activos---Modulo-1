package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.ActualizarEstadoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.DisponibilidadView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.EstadoActualizadoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.PaginaView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoFiltro;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.ReporteView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoDuplicadoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoNoEncontradoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.SolicitudInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.EstadoActualizadoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.HistorialRecursoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.RecursoQueryPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.TransactionPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.ValidacionExternaPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.TransicionEstadoInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GestionRecursosTest {

    @Mock
    private ActivoRepository activoRepository;

    @Mock
    private EspacioRepository espacioRepository;

    @Mock
    private EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    @Mock
    private EstadoActivoRepository estadoActivoRepository;

    @Mock
    private FacultadRepository facultadRepository;

    @Mock
    private UbicacionUniversitariaRepository ubicacionUniversitariaRepository;

    @Mock
    private TipoActivoRepository tipoActivoRepository;

    @Mock
    private RecursoQueryPort recursoQueryPort;

    @Mock
    private ValidacionExternaPort validacionExternaPort;

    @Mock
    private EstadoActualizadoPort estadoActualizadoPort;

    @Mock
    private HistorialRecursoPort historialRecursoPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private GestionRecursos gestionRecursos;

    private RecursoFiltro filtro;

    @BeforeEach
    void setUp() {
        filtro = RecursoFiltro.vacio();
    }

    @Test
    @DisplayName("Listar recursos paginados del catálogo")
    void listarRecursosExitoso() {
        RecursoResumenView resumen = new RecursoResumenView(
                UUID.randomUUID(), "ACT-100", "Laptop Dell", "ACTIVO", "DISPONIBLE", "OPTIMO",
                "Facultad QA", "Ubicacion QA", 10, 30, LocalDateTime.now());

        when(recursoQueryPort.listar(filtro, "nombre", "ASC", 0, 10)).thenReturn(List.of(resumen));
        when(recursoQueryPort.contar(filtro)).thenReturn(1L);

        PaginaView<RecursoResumenView> resultado = gestionRecursos.listar(filtro, 0, 10);

        assertEquals(1, resultado.content().size());
        assertEquals(1L, resultado.totalElements());
        assertEquals(0, resultado.page());
        assertEquals(10, resultado.size());
    }

    @Test
    @DisplayName("Generar previsualización del reporte con paginación calculada")
    void generarReporteExitoso() {
        when(recursoQueryPort.contar(filtro)).thenReturn(25L);
        when(recursoQueryPort.listar(filtro, "nombre", "ASC", 0, 10)).thenReturn(Collections.emptyList());

        ReporteView reporte = gestionRecursos.generarReporte(filtro, "nombre", "ASC", 0, 10);

        assertEquals(0, reporte.content().size());
        assertEquals(25L, reporte.totalElements());
        assertEquals(3, reporte.totalPaginas());
        assertEquals(0, reporte.page());
        assertEquals(10, reporte.size());
    }

    @Test
    @DisplayName("Ajustar página solicitada al reporte cuando está fuera de rango")
    void generarReportePaginaFueraDeRango() {
        when(recursoQueryPort.contar(filtro)).thenReturn(5L);
        when(recursoQueryPort.listar(filtro, "nombre", "ASC", 2, 2)).thenReturn(Collections.emptyList());

        ReporteView reporte = gestionRecursos.generarReporte(filtro, "nombre", "ASC", 3, 2);

        assertEquals(2, reporte.page());
        assertEquals(2, reporte.size());
        assertEquals(5L, reporte.totalElements());
    }

    @Test
    @DisplayName("Obtener recurso inexistente lanza excepción")
    void obtenerRecursoNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(recursoQueryPort.findDetalle(id)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> gestionRecursos.obtener(id));

        assertEquals("No se encontró un recurso con el id '" + id + "'.", ex.getMessage());
    }

    @Test
    @DisplayName("Consultar disponibilidad de recurso disponible")
    void consultarDisponibilidadDisponible() {
        UUID id = UUID.randomUUID();
        RecursoDetalleView detalle = new RecursoDetalleView(
                id, "ACT-100", "Laptop Dell", "ACTIVO", "OPTIMO", "DISPONIBLE", "Ubicacion QA", "Facultad QA", 30, 10, List.of());
        when(recursoQueryPort.findDetalle(id)).thenReturn(Optional.of(detalle));

        DisponibilidadView view = gestionRecursos.disponibilidad(id, null);

        assertEquals("DISPONIBLE", view.estado());
        assertTrue(view.disponible());
        assertNull(view.motivoNoDisponibilidad());
    }

    @Test
    @DisplayName("Consultar disponibilidad con fecha pasada lanza excepción")
    void consultarDisponibilidadFechaPasada() {
        UUID id = UUID.randomUUID();
        RecursoDetalleView detalle = new RecursoDetalleView(
                id, "ACT-100", "Laptop Dell", "ACTIVO", "OPTIMO", "DISPONIBLE", "Ubicacion QA", "Facultad QA", 30, 10, List.of());
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> gestionRecursos.disponibilidad(id, java.time.OffsetDateTime.now().minusHours(1)));

        assertEquals("La fecha y hora de consulta no puede ser anterior al momento actual.", ex.getMessage());
    }

    @Test
    @DisplayName("Consultar disponibilidad con fecha futura devuelve recurso en uso")
    void consultarDisponibilidadFechaFutura() {
        UUID id = UUID.randomUUID();
        RecursoDetalleView detalle = new RecursoDetalleView(
                id, "ACT-100", "Laptop Dell", "ACTIVO", "ESTADO", "EN_USO", "Ubicacion QA", "Facultad QA", 30, 10, List.of());
        when(recursoQueryPort.findDetalle(id)).thenReturn(Optional.of(detalle));

        DisponibilidadView view = gestionRecursos.disponibilidad(id, java.time.OffsetDateTime.now().plusDays(1));

        assertEquals("EN_USO", view.estado());
        assertFalse(view.disponible());
        assertEquals("Recurso actualmente en uso.", view.motivoNoDisponibilidad());
    }

    @Test
    @DisplayName("Crear activo exitoso registrando referencias del catálogo")
    void crearActivoExitoso() {
        UUID espacioId = UUID.randomUUID();
        UUID facultadId = UUID.randomUUID();
        UUID ubicacionId = UUID.randomUUID();
        UUID tipoActivoId = UUID.randomUUID();
        UUID estadoFisicoId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();

        Espacio espacio = Espacio.builder()
                .id(espacioId)
                .nombre("Lab Telecomunicaciones")
                .codigo("ESP-010")
                .aforo(30)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(24)
                .facultadId(facultadId)
                .disponibilidadId(disponibilidadId)
                .ubicacionId(ubicacionId)
                .build();

        UUID recursoId = UUID.randomUUID();
        RecursoDetalleView detalle = new RecursoDetalleView(
                recursoId, "ACT-900", "Microscopio #5", "ACTIVO", "OPTIMO", "DISPONIBLE", "Ubicacion QA", "Facultad QA", 30, 10, List.of());

        CrearActivoCommand command = new CrearActivoCommand(
                "Microscopio #5", "ACT-900", EstadoFisicoActivo.OPTIMO, espacioId, facultadId, tipoActivoId, 30);

        ejecutarEnTransaccion();
        when(activoRepository.existsByCodigo("ACT-900")).thenReturn(false);
        when(espacioRepository.findById(espacioId)).thenReturn(Optional.of(espacio));
        when(facultadRepository.existsById(facultadId)).thenReturn(true);
        when(estadoDisponibilidadRepository.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE))
                .thenReturn(Optional.of(EstadoDisponibilidad.builder()
                        .id(disponibilidadId)
                        .disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE)
                        .descripcion("Disponible")
                        .build()));
        when(estadoActivoRepository.findByEstado(EstadoFisicoActivo.OPTIMO))
                .thenReturn(Optional.of(EstadoActivo.builder()
                        .id(estadoFisicoId)
                        .estado(EstadoFisicoActivo.OPTIMO)
                        .descripcion("Optimo")
                        .build()));
        when(tipoActivoRepository.findById(tipoActivoId))
                .thenReturn(Optional.of(TipoActivo.builder()
                        .id(tipoActivoId)
                        .nombre("GENERAL")
                        .descripcion("Tipo de activo por defecto")
                        .plazoDevolucion(30)
                        .build()));
        when(activoRepository.save(any(Activo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(recursoQueryPort.findDetalle(any())).thenReturn(Optional.of(detalle));

        RecursoDetalleView creado = gestionRecursos.crearActivo(command);

        assertEquals(detalle.id(), creado.id());
        verify(activoRepository, times(1)).save(any(Activo.class));
    }

    @Test
    @DisplayName("Crear activo con código duplicado lanza excepción")
    void crearActivoCodigoDuplicado() {
        CrearActivoCommand command = new CrearActivoCommand(
                "Microscopio #5", "ACT-900", EstadoFisicoActivo.OPTIMO, UUID.randomUUID(), null, UUID.randomUUID(), 30);

        ejecutarEnTransaccion();
        when(activoRepository.existsByCodigo("ACT-900")).thenReturn(true);

        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> gestionRecursos.crearActivo(command));

        assertEquals("Ya existe un activo registrado con el código 'ACT-900'.", ex.getMessage());
        verify(activoRepository, never()).save(any(Activo.class));
    }

    @Test
    @DisplayName("Crear espacio exitoso registrando referencias del catálogo")
    void crearEspacioExitoso() {
        UUID facultadId = UUID.randomUUID();
        UUID ubicacionId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();

        CrearEspacioCommand command = new CrearEspacioCommand(
                "Lab Robótica", "ESP-900", 30, facultadId, ubicacionId, null, Collections.emptyList(), 24);

        UUID recursoId = UUID.randomUUID();
        RecursoDetalleView detalle = new RecursoDetalleView(
                recursoId, "ESP-900", "Lab Robótica", "ESPACIO", "OPTIMO", "DISPONIBLE", "Ubicacion QA", "Facultad QA", 24, 30, List.of());

        ejecutarEnTransaccion();
        when(espacioRepository.existsByNombre("Lab Robótica")).thenReturn(false);
        when(espacioRepository.existsByCodigo("ESP-900")).thenReturn(false);
        when(facultadRepository.existsById(facultadId)).thenReturn(true);
        when(ubicacionUniversitariaRepository.existsById(ubicacionId)).thenReturn(true);
        when(estadoDisponibilidadRepository.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE))
                .thenReturn(Optional.of(EstadoDisponibilidad.builder()
                        .id(disponibilidadId)
                        .disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE)
                        .descripcion("Disponible")
                        .build()));
        when(espacioRepository.save(any(Espacio.class))).thenAnswer(inv -> inv.getArgument(0));
        when(recursoQueryPort.findDetalle(any())).thenReturn(Optional.of(detalle));

        RecursoDetalleView creado = gestionRecursos.crearEspacio(command);

        assertEquals(detalle.id(), creado.id());
        verify(espacioRepository, times(1)).save(any(Espacio.class));
    }

    @Test
    @DisplayName("Crear espacio con nombre duplicado lanza excepción")
    void crearEspacioNombreDuplicado() {
        CrearEspacioCommand command = new CrearEspacioCommand(
                "Lab QA", "ESP-900", 30, UUID.randomUUID(), UUID.randomUUID(), null, Collections.emptyList(), 24);

        ejecutarEnTransaccion();
        when(espacioRepository.existsByNombre("Lab QA")).thenReturn(true);

        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> gestionRecursos.crearEspacio(command));

        assertEquals("Ya existe un espacio registrado con el nombre 'Lab QA'.", ex.getMessage());
        verify(espacioRepository, never()).save(any(Espacio.class));
    }

    @Test
    @DisplayName("Actualizar estado del activo con transición válida")
    void actualizarEstadoExitoso() {
        UUID recursoId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();
        UUID estadoFisicoId = UUID.randomUUID();

        Activo activo = Activo.builder()
                .id(recursoId)
                .nombre("Proyector Epson")
                .codigo("ACT-200")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .plazoDevolucion(30)
                .build();

        EstadoDisponibilidad estadoAnterior = EstadoDisponibilidad.builder()
                .id(disponibilidadId)
                .disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE)
                .descripcion("Disponible")
                .build();
        EstadoDisponibilidad estadoNuevo = EstadoDisponibilidad.builder()
                .id(UUID.randomUUID())
                .disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO)
                .descripcion("En uso")
                .build();

        ejecutarEnTransaccion();
        when(activoRepository.findByIdForUpdate(recursoId)).thenReturn(Optional.of(activo));
        when(estadoDisponibilidadRepository.findById(disponibilidadId)).thenReturn(Optional.of(estadoAnterior));
        when(estadoDisponibilidadRepository.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO))
                .thenReturn(Optional.of(estadoNuevo));
        when(activoRepository.save(any(Activo.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarEstadoCommand command = new ActualizarEstadoCommand(
                EstadoDisponibilidadRecurso.EN_USO, "Mantenimiento programado", "admin");
        EstadoActualizadoView view = gestionRecursos.actualizarEstado(recursoId, command, "corr-1");

        assertEquals(recursoId, view.id());
        assertEquals("ACTIVO", view.tipo());
        assertEquals("DISPONIBLE", view.estadoAnterior());
        assertEquals("EN_USO", view.estadoNuevo());
        assertEquals("admin", view.usuarioResponsable());
        verify(estadoActualizadoPort).publicar(
                eq("corr-1"), eq(recursoId), eq("ACTIVO"), eq("DISPONIBLE"), eq("EN_USO"),
                eq("Mantenimiento programado"), eq("admin"));
        verify(historialRecursoPort).registrar(
                eq(recursoId), eq("DISPONIBLE"), eq("EN_USO"), eq("admin"), any(LocalDateTime.class), eq("Mantenimiento programado"));
    }

    @Test
    @DisplayName("Actualizar estado de recurso inexistente lanza excepción")
    void actualizarEstadoRecursoNoEncontrado() {
        UUID recursoId = UUID.randomUUID();
        ActualizarEstadoCommand command = new ActualizarEstadoCommand(
                EstadoDisponibilidadRecurso.EN_USO, "Mantenimiento", "admin");

        ejecutarEnTransaccion();
        when(activoRepository.findByIdForUpdate(recursoId)).thenReturn(Optional.empty());
        when(espacioRepository.findByIdForUpdate(recursoId)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> gestionRecursos.actualizarEstado(recursoId, command, "corr-1"));

        assertEquals("No se encontró un recurso con el id '" + recursoId + "'.", ex.getMessage());
    }

    @Test
    @DisplayName("Actualizar estado con transición inválida lanza excepción")
    void actualizarEstadoTransicionInvalida() {
        UUID recursoId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();

        Activo activo = Activo.builder()
                .id(recursoId)
                .nombre("Proyector Epson")
                .codigo("ACT-200")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(UUID.randomUUID())
                .disponibilidadId(disponibilidadId)
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .plazoDevolucion(30)
                .build();

        EstadoDisponibilidad estadoAnterior = EstadoDisponibilidad.builder()
                .id(disponibilidadId)
                .disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_MANTENIMIENTO)
                .descripcion("En mantenimiento")
                .build();

        ejecutarEnTransaccion();
        when(activoRepository.findByIdForUpdate(recursoId)).thenReturn(Optional.of(activo));
        when(estadoDisponibilidadRepository.findById(disponibilidadId)).thenReturn(Optional.of(estadoAnterior));

        ActualizarEstadoCommand command = new ActualizarEstadoCommand(
                EstadoDisponibilidadRecurso.EN_USO, "Mantenimiento", "admin");

        TransicionEstadoInvalidaException ex = assertThrows(TransicionEstadoInvalidaException.class,
                () -> gestionRecursos.actualizarEstado(recursoId, command, "corr-1"));

        assertEquals("Transición inválida: no se puede pasar de EN_MANTENIMIENTO a EN_USO.", ex.getMessage());
        verify(estadoActualizadoPort, never()).publicar(any(), any(), any(), any(), any(), any(), any());
    }

    private void ejecutarEnTransaccion() {
        when(transactionPort.execute(ArgumentMatchers.<Supplier<Object>>any()))
                .thenAnswer(invocation -> {
                    Supplier<Object> supplier = (Supplier<Object>) invocation.getArgument(0);
                    return supplier.get();
                });
    }
}
