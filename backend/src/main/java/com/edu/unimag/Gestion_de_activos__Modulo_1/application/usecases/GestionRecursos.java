package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.ActualizarEstadoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.DisponibilidadView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.EstadoActualizadoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.PaginaView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.ConflictoValidacionExternaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.EntidadRelacionadaInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoDuplicadoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoNoEncontradoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.SolicitudInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.EstadoActualizadoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.HistorialRecursoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.RecursoQueryPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.TransactionPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.ValidacionExternaPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.RecursoStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Casos de uso del catálogo de recursos: consulta, registro y actualización
 * manual de disponibilidad.
 *
 * <p>Depende únicamente de puertos de dominio y de aplicación, de modo que la
 * capa de infraestructura (REST/Kafka/PostgreSQL) no queda acoplada a la lógica
 * de negocio.</p>
 */
public class GestionRecursos {

    private static final Logger log = LoggerFactory.getLogger(GestionRecursos.class);

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;
    private final EstadoActivoRepository estadoActivoRepository;
    private final FacultadRepository facultadRepository;
    private final UbicacionUniversitariaRepository ubicacionRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final RecursoQueryPort recursoQueryPort;
    private final ValidacionExternaPort validacionExternaPort;
    private final EstadoActualizadoPort estadoActualizadoPort;
    private final HistorialRecursoPort historialRecursoPort;
    private final TransactionPort transactionPort;

    public GestionRecursos(ActivoRepository activoRepository,
                           EspacioRepository espacioRepository,
                           EstadoDisponibilidadRepository estadoDisponibilidadRepository,
                           EstadoActivoRepository estadoActivoRepository,
                           FacultadRepository facultadRepository,
                           UbicacionUniversitariaRepository ubicacionRepository,
                           TipoActivoRepository tipoActivoRepository,
                           RecursoQueryPort recursoQueryPort,
                           ValidacionExternaPort validacionExternaPort,
                           EstadoActualizadoPort estadoActualizadoPort,
                           HistorialRecursoPort historialRecursoPort,
                           TransactionPort transactionPort) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
        this.estadoActivoRepository = estadoActivoRepository;
        this.facultadRepository = facultadRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.tipoActivoRepository = tipoActivoRepository;
        this.recursoQueryPort = recursoQueryPort;
        this.validacionExternaPort = validacionExternaPort;
        this.estadoActualizadoPort = estadoActualizadoPort;
        this.historialRecursoPort = historialRecursoPort;
        this.transactionPort = transactionPort;
    }

    public PaginaView<RecursoResumenView> listar(String tipo,
                                                 String estado,
                                                 UUID facultadId,
                                                 String facultadNombre,
                                                 int page,
                                                 int size) {
        List<RecursoResumenView> content = recursoQueryPort.listar(
                tipo, estado, facultadId, facultadNombre, page, size);
        long total = recursoQueryPort.contar(tipo, estado, facultadId, facultadNombre);
        return new PaginaView<>(content, page, size, total);
    }

    public RecursoDetalleView obtener(UUID id) {
        return recursoQueryPort.findDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un recurso con el id '" + id + "'."));
    }

    public DisponibilidadView disponibilidad(UUID id) {
        RecursoDetalleView recurso = obtener(id);
        EstadoDisponibilidadRecurso estado = parseEstadoAlmacenado(recurso.estado());

        return new DisponibilidadView(
                recurso.id(),
                estado.name(),
                estado == EstadoDisponibilidadRecurso.DISPONIBLE,
                null,
                motivoNoDisponibilidad(estado));
    }

    public RecursoDetalleView crearActivo(CrearActivoCommand command) {
        return transactionPort.execute(() -> {
            String codigo = command.codigo().trim();
            if (activoRepository.existsByCodigo(codigo)) {
                throw new RecursoDuplicadoException(
                        "Ya existe un activo registrado con el código '" + codigo + "'.");
            }

            Espacio espacio = espacioRepository.findById(command.ubicacionId())
                    .orElseThrow(() -> new EntidadRelacionadaInvalidaException(
                            "El espacio indicado en 'ubicacionId' no existe: " + command.ubicacionId()));

            validarFacultad(command.facultadId());
            if (command.facultadId() != null && !command.facultadId().equals(espacio.getFacultadId())) {
                throw new EntidadRelacionadaInvalidaException(
                        "La facultad indicada no corresponde a la facultad del espacio seleccionado.");
            }

            UUID disponibilidadId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.DISPONIBLE);
            UUID estadoFisicoId = obtenerEstadoFisicoId(command.estadoFisico());
            UUID tipoActivoId = obtenerTipoActivoId(command.tipoActivoId());

            Activo activo = Activo.builder()
                    .nombre(command.nombre().trim())
                    .codigo(codigo)
                    .categoria(CategoriaRecurso.ACTIVO)
                    .plazoDevolucion(command.plazoDevolucion() != null ? command.plazoDevolucion() : 0)
                    .estadoFisicoId(estadoFisicoId)
                    .disponibilidadId(disponibilidadId)
                    .tipoActivoId(tipoActivoId)
                    .espacioId(espacio.getId())
                    .build();

            Activo guardado = activoRepository.save(activo);
            log.info("Activo creado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());
            return obtener(guardado.getId());
        });
    }

    public RecursoDetalleView crearEspacio(CrearEspacioCommand command) {
        return transactionPort.execute(() -> {
            String nombre = command.nombre().trim();
            if (espacioRepository.existsByNombre(nombre)) {
                throw new RecursoDuplicadoException(
                        "Ya existe un espacio registrado con el nombre '" + nombre + "'.");
            }

            String codigo = (command.codigo() == null || command.codigo().isBlank())
                    ? generarCodigoEspacio()
                    : command.codigo().trim();
            if (espacioRepository.existsByCodigo(codigo)) {
                throw new RecursoDuplicadoException(
                        "Ya existe un espacio registrado con el código '" + codigo + "'.");
            }

            validarFacultad(command.facultadId());
            UUID ubicacionId = resolverUbicacionId(command.ubicacionId(), command.edificio());
            List<UUID> equipamiento = validarEquipamiento(command.equipamiento());
            UUID disponibilidadId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.DISPONIBLE);

            Espacio espacio = Espacio.builder()
                    .nombre(nombre)
                    .codigo(codigo)
                    .categoria(CategoriaRecurso.ESPACIO)
                    .aforo(command.aforoMaximo())
                    .plazoDevolucion(command.plazoDevolucion() != null ? command.plazoDevolucion() : 0)
                    .equipamiento(equipamiento)
                    .facultadId(command.facultadId())
                    .ubicacionId(ubicacionId)
                    .disponibilidadId(disponibilidadId)
                    .build();

            Espacio guardado = espacioRepository.save(espacio);
            log.info("Espacio creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
            return obtener(guardado.getId());
        });
    }

    public EstadoActualizadoView actualizarEstado(UUID id,
                                                  ActualizarEstadoCommand command,
                                                  String correlationId) {
        return transactionPort.execute(() -> {
            Optional<Activo> activoOpt = activoRepository.findByIdForUpdate(id);
            Optional<Espacio> espacioOpt = activoOpt.isPresent()
                    ? Optional.empty()
                    : espacioRepository.findByIdForUpdate(id);

            if (activoOpt.isEmpty() && espacioOpt.isEmpty()) {
                throw new RecursoNoEncontradoException(
                        "No se encontró un recurso con el id '" + id + "'.");
            }

            String tipo = activoOpt.isPresent()
                    ? CategoriaRecurso.ACTIVO.name()
                    : CategoriaRecurso.ESPACIO.name();
            UUID disponibilidadActualId = activoOpt.map(Activo::getDisponibilidadId)
                    .orElseGet(() -> espacioOpt.map(Espacio::getDisponibilidadId).orElse(null));
            EstadoDisponibilidadRecurso estadoAnterior = obtenerEstadoDisponibilidad(disponibilidadActualId);
            EstadoDisponibilidadRecurso estadoNuevo = command.nuevoEstado();

            RecursoStateMachine.validarTransicion(estadoAnterior, estadoNuevo);
            validarDependenciasExternas(id, estadoNuevo);

            UUID nuevoEstadoId = obtenerEstadoDisponibilidadId(estadoNuevo);
            activoOpt.ifPresent(activo -> activoRepository.save(activo.conDisponibilidad(nuevoEstadoId)));
            espacioOpt.ifPresent(espacio -> espacioRepository.save(espacio.conDisponibilidad(nuevoEstadoId)));

            OffsetDateTime timestamp = OffsetDateTime.now();
            String motivo = (command.motivo() == null || command.motivo().isBlank())
                    ? "Actualización manual de estado"
                    : command.motivo().trim();
            String usuario = command.usuarioResponsable().trim();

            historialRecursoPort.registrar(
                    id,
                    estadoAnterior.name(),
                    estadoNuevo.name(),
                    usuario,
                    LocalDateTime.now(),
                    motivo);

            transactionPort.afterCommit(() -> estadoActualizadoPort.publicar(
                    correlationId,
                    id,
                    tipo,
                    estadoAnterior.name(),
                    estadoNuevo.name(),
                    motivo,
                    usuario));

            log.info("Estado actualizado manualmente: recurso={}, {} -> {}, usuario={}",
                    id, estadoAnterior, estadoNuevo, usuario);

            return new EstadoActualizadoView(
                    id,
                    tipo,
                    estadoAnterior.name(),
                    estadoNuevo.name(),
                    usuario,
                    timestamp);
        });
    }

    private void validarDependenciasExternas(UUID recursoId, EstadoDisponibilidadRecurso nuevoEstado) {
        if (nuevoEstado != EstadoDisponibilidadRecurso.DISPONIBLE
                && nuevoEstado != EstadoDisponibilidadRecurso.EN_MANTENIMIENTO) {
            return;
        }

        Optional<Boolean> reserva = validacionExternaPort.tieneReservaActiva(recursoId);
        if (reserva.isEmpty()) {
            throw new ConflictoValidacionExternaException(
                    "No fue posible validar las reservas activas del recurso en el Módulo 2. "
                            + "La transición no se ejecutó; intente nuevamente.");
        }
        if (reserva.get()) {
            throw new ConflictoValidacionExternaException(
                    "El recurso tiene una reserva activa en el Módulo 2; la transición no se ejecutó.");
        }

        if (nuevoEstado == EstadoDisponibilidadRecurso.DISPONIBLE) {
            Optional<Boolean> novedad = validacionExternaPort.tieneNovedadPendiente(recursoId);
            if (novedad.isEmpty()) {
                throw new ConflictoValidacionExternaException(
                        "No fue posible validar las novedades técnicas del recurso en el Módulo 3. "
                                + "La transición no se ejecutó; intente nuevamente.");
            }
            if (novedad.get()) {
                throw new ConflictoValidacionExternaException(
                        "El recurso tiene novedades técnicas pendientes en el Módulo 3; "
                                + "la transición no se ejecutó.");
            }
        }
    }

    private UUID resolverUbicacionId(UUID ubicacionId, String edificio) {
        if (ubicacionId != null) {
            if (!ubicacionRepository.existsById(ubicacionId)) {
                throw new EntidadRelacionadaInvalidaException(
                        "La ubicación indicada no existe: " + ubicacionId);
            }
            return ubicacionId;
        }

        if (edificio != null && !edificio.isBlank()) {
            String nombre = edificio.trim();
            return ubicacionRepository.findByNombre(nombre)
                    .map(UbicacionUniversitaria::getId)
                    .orElseGet(() -> ubicacionRepository.save(
                            UbicacionUniversitaria.builder().nombre(nombre).build()).getId());
        }

        throw new SolicitudInvalidaException(
                "Debe indicar 'ubicacionId' o 'edificio' para ubicar el espacio.");
    }

    private List<UUID> validarEquipamiento(List<UUID> equipamiento) {
        if (equipamiento == null || equipamiento.isEmpty()) {
            return List.of();
        }

        Set<UUID> unicos = new LinkedHashSet<>(equipamiento);
        for (UUID activoId : unicos) {
            if (!activoRepository.existsById(activoId)) {
                throw new EntidadRelacionadaInvalidaException(
                        "El activo de equipamiento no existe: " + activoId);
            }
        }
        return new ArrayList<>(unicos);
    }

    private void validarFacultad(UUID facultadId) {
        if (facultadId != null && !facultadRepository.existsById(facultadId)) {
            throw new EntidadRelacionadaInvalidaException(
                    "La facultad indicada no existe: " + facultadId);
        }
    }

    private UUID obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso estado) {
        return estadoDisponibilidadRepository.findByDisponibilidadRecurso(estado)
                .map(EstadoDisponibilidad::getId)
                .orElseGet(() -> estadoDisponibilidadRepository.save(
                        EstadoDisponibilidad.builder()
                                .disponibilidadRecurso(estado)
                                .descripcion(estado.name())
                                .build()).getId());
    }

    private UUID obtenerEstadoFisicoId(EstadoFisicoActivo estado) {
        return estadoActivoRepository.findByEstado(estado)
                .map(EstadoActivo::getId)
                .orElseGet(() -> estadoActivoRepository.save(
                        EstadoActivo.builder()
                                .estado(estado)
                                .descripcion(estado.name())
                                .build()).getId());
    }

    private UUID obtenerTipoActivoId(UUID tipoActivoId) {
        if (tipoActivoId != null) {
            return tipoActivoRepository.findById(tipoActivoId)
                    .map(TipoActivo::getId)
                    .orElseThrow(() -> new EntidadRelacionadaInvalidaException(
                            "El tipo de activo indicado no existe: " + tipoActivoId));
        }

        return tipoActivoRepository.findByNombre("GENERAL")
                .map(TipoActivo::getId)
                .orElseGet(() -> tipoActivoRepository.save(
                        TipoActivo.builder()
                                .nombre("GENERAL")
                                .descripcion("Tipo de activo por defecto")
                                .plazoDevolucion(0)
                                .build()).getId());
    }

    private String generarCodigoEspacio() {
        String sufijo = UUID.randomUUID().toString().replace("-", "")
                .substring(0, 8)
                .toUpperCase();
        return "ESP-" + sufijo;
    }

    private EstadoDisponibilidadRecurso obtenerEstadoDisponibilidad(UUID disponibilidadId) {
        if (disponibilidadId == null) {
            throw new IllegalStateException("El recurso no tiene un estado de disponibilidad asociado.");
        }
        return estadoDisponibilidadRepository.findById(disponibilidadId)
                .map(EstadoDisponibilidad::getDisponibilidadRecurso)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el estado de disponibilidad con id " + disponibilidadId));
    }

    private EstadoDisponibilidadRecurso parseEstadoAlmacenado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalStateException("El recurso no tiene un estado de disponibilidad asociado.");
        }
        try {
            return EstadoDisponibilidadRecurso.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Estado de disponibilidad desconocido: " + estado);
        }
    }

    private String motivoNoDisponibilidad(EstadoDisponibilidadRecurso estado) {
        return switch (estado) {
            case DISPONIBLE -> null;
            case EN_USO -> "Recurso actualmente en uso.";
            case EN_MANTENIMIENTO -> "Recurso en mantenimiento.";
            case RESERVADO -> "Recurso reservado.";
            case BLOQUEO_ACADEMICO -> "Recurso con bloqueo académico.";
        };
    }
}
