package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.service;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ActualizarEstadoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearActivoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearEspacioRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.DisponibilidadResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.EstadoActualizadoResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.PageResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.ConflictoValidacionExternaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.EntidadRelacionadaInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.EstadoSinCambioException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.RecursoDuplicadoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.RecursoNoEncontradoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.SolicitudInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.repository.RecursoReadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.ActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoDisponibilidadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EspacioJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.FacultadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.HistorialRecursoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.TipoActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.UbicacionJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.HistorialRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Ubicacion;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.Modulo2Client;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.Modulo3Client;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto.NovedadPendienteResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto.ReservaActivaResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer.EstadoActualizadoProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Casos de uso de la API REST: catálogo, disponibilidad, registro y
 * actualización manual de estado.
 *
 * <p>La actualización manual escribe en PostgreSQL (estado + historial) y, una
 * vez confirmado el commit, publica el evento {@code estado.actualizado} para
 * que M2 y M3 mantengan sincronizada su información.</p>
 */
@Service
public class RecursoApiService {

    private static final Logger log = LoggerFactory.getLogger(RecursoApiService.class);

    private final RecursoReadRepository recursoReadRepository;
    private final ActivoJpaRepository activoRepository;
    private final EspacioJpaRepository espacioRepository;
    private final EstadoDisponibilidadJpaRepository estadoDisponibilidadRepository;
    private final EstadoActivoJpaRepository estadoActivoRepository;
    private final FacultadJpaRepository facultadRepository;
    private final UbicacionJpaRepository ubicacionRepository;
    private final TipoActivoJpaRepository tipoActivoRepository;
    private final HistorialRecursoJpaRepository historialRepository;
    private final EstadoActualizadoProducer estadoActualizadoProducer;
    private final Modulo2Client modulo2Client;
    private final Modulo3Client modulo3Client;

    @Value("${external.validation.enabled:true}")
    private boolean validacionExternaHabilitada;

    public RecursoApiService(RecursoReadRepository recursoReadRepository,
                             ActivoJpaRepository activoRepository,
                             EspacioJpaRepository espacioRepository,
                             EstadoDisponibilidadJpaRepository estadoDisponibilidadRepository,
                             EstadoActivoJpaRepository estadoActivoRepository,
                             FacultadJpaRepository facultadRepository,
                             UbicacionJpaRepository ubicacionRepository,
                             TipoActivoJpaRepository tipoActivoRepository,
                             HistorialRecursoJpaRepository historialRepository,
                             EstadoActualizadoProducer estadoActualizadoProducer,
                             Modulo2Client modulo2Client,
                             Modulo3Client modulo3Client) {
        this.recursoReadRepository = recursoReadRepository;
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
        this.estadoActivoRepository = estadoActivoRepository;
        this.facultadRepository = facultadRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.tipoActivoRepository = tipoActivoRepository;
        this.historialRepository = historialRepository;
        this.estadoActualizadoProducer = estadoActualizadoProducer;
        this.modulo2Client = modulo2Client;
        this.modulo3Client = modulo3Client;
    }

    @Transactional(readOnly = true)
    public PageResponse<RecursoResumenResponse> listar(String tipo,
                                                        String estado,
                                                        UUID facultadId,
                                                        String facultadNombre,
                                                        int page,
                                                        int size) {
        List<RecursoResumenResponse> content = recursoReadRepository.listar(
                tipo, estado, facultadId, facultadNombre, page, size);
        long total = recursoReadRepository.contar(tipo, estado, facultadId, facultadNombre);

        return new PageResponse<>(content, page, size, total);
    }

    @Transactional(readOnly = true)
    public RecursoDetalleResponse obtener(UUID id) {
        return recursoReadRepository.findDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un recurso con el id '" + id + "'."));
    }

    @Transactional(readOnly = true)
    public DisponibilidadResponse disponibilidad(UUID id) {
        RecursoDetalleResponse recurso = obtener(id);
        EstadoDisponibilidadRecurso estado = parseEstadoAlmacenado(recurso.estado());

        boolean disponible = estado == EstadoDisponibilidadRecurso.DISPONIBLE;
        return new DisponibilidadResponse(
                recurso.id(),
                estado.name(),
                disponible,
                null,
                motivoNoDisponibilidad(estado));
    }

    @Transactional
    public RecursoDetalleResponse crearActivo(CrearActivoRequest request) {
        String codigo = request.codigo().trim();
        if (activoRepository.existsByCodigo(codigo)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un activo registrado con el código '" + codigo + "'.");
        }

        Espacio espacio = espacioRepository.findById(request.ubicacionId())
                .orElseThrow(() -> new EntidadRelacionadaInvalidaException(
                        "El espacio indicado en 'ubicacionId' no existe: " + request.ubicacionId()));

        validarFacultad(request.facultadId());
        if (request.facultadId() != null && !request.facultadId().equals(espacio.getFacultadId())) {
            throw new EntidadRelacionadaInvalidaException(
                    "La facultad indicada no corresponde a la facultad del espacio seleccionado.");
        }

        UUID disponibilidadId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.DISPONIBLE);
        UUID estadoFisicoId = obtenerEstadoFisicoId(request.estadoFisico());
        UUID tipoActivoId = obtenerTipoActivoId(request.tipoActivoId());

        Activo activo = Activo.builder()
                .nombre(request.nombre().trim())
                .codigo(codigo)
                .categoria(CategoriaRecurso.ACTIVO)
                .plazoDevolucion(request.plazoDevolucion() != null ? request.plazoDevolucion() : 0)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .tipoActivoId(tipoActivoId)
                .espacio(espacio)
                .build();

        Activo guardado = activoRepository.saveAndFlush(activo);
        log.info("Activo creado: id={}, codigo={}", guardado.getId(), guardado.getCodigo());

        return recursoReadRepository.findDetalle(guardado.getId())
                .orElseThrow(() -> new IllegalStateException("No fue posible leer el activo recién creado."));
    }

    @Transactional
    public RecursoDetalleResponse crearEspacio(CrearEspacioRequest request) {
        String nombre = request.nombre().trim();
        if (espacioRepository.existsByNombre(nombre)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un espacio registrado con el nombre '" + nombre + "'.");
        }

        String codigo = (request.codigo() == null || request.codigo().isBlank())
                ? generarCodigoEspacio()
                : request.codigo().trim();
        if (espacioRepository.existsByCodigo(codigo)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un espacio registrado con el código '" + codigo + "'.");
        }

        validarFacultad(request.facultadId());

        UUID ubicacionId = resolverUbicacionId(request);
        List<UUID> equipamiento = validarEquipamiento(request.equipamiento());
        UUID disponibilidadId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.DISPONIBLE);

        Espacio espacio = Espacio.builder()
                .nombre(nombre)
                .codigo(codigo)
                .categoria(CategoriaRecurso.ESPACIO)
                .aforoMaximo(request.aforoMaximo())
                .plazoDevolucion(request.plazoDevolucion() != null ? request.plazoDevolucion() : 0)
                .equipamiento(new ArrayList<>(equipamiento))
                .facultadId(request.facultadId())
                .ubicacionId(ubicacionId)
                .disponibilidadId(disponibilidadId)
                .build();

        Espacio guardado = espacioRepository.saveAndFlush(espacio);
        log.info("Espacio creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());

        return recursoReadRepository.findDetalle(guardado.getId())
                .orElseThrow(() -> new IllegalStateException("No fue posible leer el espacio recién creado."));
    }

    @Transactional
    public EstadoActualizadoResponse actualizarEstado(UUID id,
                                                       ActualizarEstadoRequest request,
                                                       String correlationId) {
        Optional<Activo> activoOpt = activoRepository.findByIdForUpdate(id);
        Optional<Espacio> espacioOpt = activoOpt.isPresent()
                ? Optional.empty()
                : espacioRepository.findByIdForUpdate(id);

        if (activoOpt.isEmpty() && espacioOpt.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No se encontró un recurso con el id '" + id + "'.");
        }

        String tipo = activoOpt.isPresent() ? CategoriaRecurso.ACTIVO.name() : CategoriaRecurso.ESPACIO.name();
        UUID disponibilidadActualId = activoOpt.map(Activo::getDisponibilidadId)
                .orElseGet(() -> espacioOpt.map(Espacio::getDisponibilidadId).orElse(null));
        EstadoDisponibilidadRecurso estadoAnterior = obtenerEstadoDisponibilidad(disponibilidadActualId);
        EstadoDisponibilidadRecurso estadoNuevo = request.nuevoEstado();

        if (estadoAnterior == estadoNuevo) {
            throw new EstadoSinCambioException(
                    "El recurso ya se encuentra en el estado seleccionado: " + estadoNuevo + ".");
        }

        RecursoStateMachine.validarTransicion(estadoAnterior, estadoNuevo);
        validarDependenciasExternas(id, estadoNuevo);

        UUID nuevoEstadoId = obtenerEstadoDisponibilidadId(estadoNuevo);
        activoOpt.ifPresent(activo -> {
            activo.setDisponibilidadId(nuevoEstadoId);
            activoRepository.save(activo);
        });
        espacioOpt.ifPresent(espacio -> {
            espacio.setDisponibilidadId(nuevoEstadoId);
            espacioRepository.save(espacio);
        });

        OffsetDateTime timestamp = OffsetDateTime.now();
        String motivo = (request.motivo() == null || request.motivo().isBlank())
                ? "Actualización manual de estado"
                : request.motivo().trim();
        String usuario = request.usuarioResponsable().trim();

        registrarHistorial(id, estadoAnterior, estadoNuevo, usuario, motivo);

        publicarEventoDespuesDeCommit(correlationId, id, tipo, estadoAnterior, estadoNuevo, motivo, usuario);

        log.info("Estado actualizado manualmente: recurso={}, {} -> {}, usuario={}",
                id, estadoAnterior, estadoNuevo, usuario);

        return new EstadoActualizadoResponse(
                id.toString(),
                tipo,
                estadoAnterior.name(),
                estadoNuevo.name(),
                usuario,
                timestamp);
    }

    private void validarDependenciasExternas(UUID recursoId, EstadoDisponibilidadRecurso nuevoEstado) {
        if (!validacionExternaHabilitada
                || (nuevoEstado != EstadoDisponibilidadRecurso.DISPONIBLE
                && nuevoEstado != EstadoDisponibilidadRecurso.EN_MANTENIMIENTO)) {
            return;
        }

        Optional<ReservaActivaResponse> reserva = modulo2Client.consultarReservaActiva(recursoId);
        if (reserva.isEmpty()) {
            throw new ConflictoValidacionExternaException(
                    "No fue posible validar las reservas activas del recurso en el Módulo 2. "
                            + "La transición no se ejecutó; intente nuevamente.");
        }
        if (reserva.get().isTieneReservaActiva()) {
            throw new ConflictoValidacionExternaException(
                    "El recurso tiene una reserva activa en el Módulo 2; la transición no se ejecutó.");
        }

        if (nuevoEstado == EstadoDisponibilidadRecurso.DISPONIBLE) {
            Optional<NovedadPendienteResponse> novedad = modulo3Client.consultarNovedadPendiente(recursoId);
            if (novedad.isEmpty()) {
                throw new ConflictoValidacionExternaException(
                        "No fue posible validar las novedades técnicas del recurso en el Módulo 3. "
                                + "La transición no se ejecutó; intente nuevamente.");
            }
            if (novedad.get().isTieneNovedadPendiente()) {
                throw new ConflictoValidacionExternaException(
                        "El recurso tiene novedades técnicas pendientes en el Módulo 3; "
                                + "la transición no se ejecutó.");
            }
        }
    }

    private UUID resolverUbicacionId(CrearEspacioRequest request) {
        if (request.ubicacionId() != null) {
            if (!ubicacionRepository.existsById(request.ubicacionId())) {
                throw new EntidadRelacionadaInvalidaException(
                        "La ubicación indicada no existe: " + request.ubicacionId());
            }
            return request.ubicacionId();
        }

        if (request.edificio() != null && !request.edificio().isBlank()) {
            String edificio = request.edificio().trim();
            return ubicacionRepository.findByNombre(edificio)
                    .map(Ubicacion::getId)
                    .orElseGet(() -> ubicacionRepository.saveAndFlush(
                            Ubicacion.builder().nombre(edificio).build()).getId());
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
                .orElseGet(() -> estadoDisponibilidadRepository.saveAndFlush(
                        EstadoDisponibilidad.builder()
                                .disponibilidadRecurso(estado)
                                .descripcion(estado.name())
                                .build()).getId());
    }

    private UUID obtenerEstadoFisicoId(EstadoFisicoActivo estado) {
        return estadoActivoRepository.findByEstado(estado)
                .map(EstadoActivo::getId)
                .orElseGet(() -> estadoActivoRepository.saveAndFlush(
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
                .orElseGet(() -> tipoActivoRepository.saveAndFlush(
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

    private void registrarHistorial(UUID recursoId,
                                    EstadoDisponibilidadRecurso estadoAnterior,
                                    EstadoDisponibilidadRecurso estadoNuevo,
                                    String usuario,
                                    String motivo) {
        HistorialRecurso historial = HistorialRecurso.builder()
                .idRecurso(recursoId)
                .estadoAnterior(estadoAnterior.name())
                .estadoNuevo(estadoNuevo.name())
                .usuarioResponsable(usuario)
                .fechaEvento(LocalDateTime.now())
                .motivo(motivo)
                .build();
        historialRepository.save(historial);
    }

    private void publicarEventoDespuesDeCommit(String correlationId,
                                               UUID recursoId,
                                               String tipo,
                                               EstadoDisponibilidadRecurso estadoAnterior,
                                               EstadoDisponibilidadRecurso estadoNuevo,
                                               String motivo,
                                               String usuario) {
        Runnable publicar = () -> estadoActualizadoProducer.publicarEstadoActualizado(
                correlationId,
                recursoId,
                tipo,
                estadoAnterior.name(),
                estadoNuevo.name(),
                motivo,
                usuario);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publicar.run();
                }
            });
        } else {
            publicar.run();
        }
    }
}
