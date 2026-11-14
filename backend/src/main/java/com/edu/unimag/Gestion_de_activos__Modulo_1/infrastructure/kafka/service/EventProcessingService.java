package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.RecursoStateMachine;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.ActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EspacioJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoDisponibilidadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EventosProcesadosJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.HistorialRecursoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventosProcesados;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.HistorialRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.DamageReportData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.EndedReservationData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.UseStartedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio transaccional ACID para el procesamiento de eventos Kafka.
 * Implementa el patrón Transactional Inbox, idempotencia con bloqueo pesimista,
 * validación de transiciones de estado, actualización física y trazabilidad en historial_recurso.
 */
@Service
public class EventProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EventProcessingService.class);

    private final EventosProcesadosJpaRepository eventosProcesadosRepo;
    private final ActivoJpaRepository activoRepo;
    private final EspacioJpaRepository espacioRepo;
    private final EstadoDisponibilidadJpaRepository estadoDisponibilidadRepo;
    private final EstadoActivoJpaRepository estadoActivoRepo;
    private final HistorialRecursoJpaRepository historialRecursoRepo;

    public EventProcessingService(
            EventosProcesadosJpaRepository eventosProcesadosRepo,
            ActivoJpaRepository activoRepo,
            EspacioJpaRepository espacioRepo,
            EstadoDisponibilidadJpaRepository estadoDisponibilidadRepo,
            EstadoActivoJpaRepository estadoActivoRepo,
            HistorialRecursoJpaRepository historialRecursoRepo) {
        this.eventosProcesadosRepo = eventosProcesadosRepo;
        this.activoRepo = activoRepo;
        this.espacioRepo = espacioRepo;
        this.estadoDisponibilidadRepo = estadoDisponibilidadRepo;
        this.estadoActivoRepo = estadoActivoRepo;
        this.historialRecursoRepo = historialRecursoRepo;
    }

    /**
     * Procesa el evento 'notificacion.uso' (M2 -> M1).
     * Transiciona el recurso a EN_USO.
     */
    @Transactional
    public void procesarNotificacionUso(ResourceEventPayload<UseStartedData> event, String rawPayload) {
        UUID eventoId = parseUUID(event.getEventId(), "eventId");
        UseStartedData data = event.getData();
        if (data == null || data.getResourceId() == null) {
            throw new IllegalArgumentException("El evento notificacion.uso carece de datos del recurso.");
        }

        UUID recursoId = data.getResourceId();
        CategoriaRecurso categoria = resolverCategoria(data.getResourceCategory(), recursoId);
        LocalDateTime fechaEvento = event.getTimestamp() != null ? event.getTimestamp().toLocalDateTime() : LocalDateTime.now();
        String usuarioResponsable = data.getReservationId() != null ? "RESERVA:" + data.getReservationId() : "MODULO_2";

        EstadoDisponibilidadRecurso estadoAnteriorEnum;
        UUID nuevoEstadoDispId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.EN_USO);

        if (categoria == CategoriaRecurso.ACTIVO) {
            // El bloqueo de la fila se toma antes de comprobar la idempotencia: dos
            // entregas concurrentes del mismo recurso se serializan y la segunda ya
            // observa el registro en eventos_procesados.
            Activo activo = activoRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento notificacion.uso duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(activo.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.EN_USO);

            activo.setDisponibilidadId(nuevoEstadoDispId);
            activoRepo.save(activo);
        } else {
            Espacio espacio = espacioRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento notificacion.uso duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(espacio.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.EN_USO);

            espacio.setDisponibilidadId(nuevoEstadoDispId);
            espacioRepo.save(espacio);
        }

        registrarHistorial(
                recursoId,
                estadoAnteriorEnum.name(),
                EstadoDisponibilidadRecurso.EN_USO.name(),
                usuarioResponsable,
                fechaEvento,
                "Inicio de ocupación/retiro - Tipo: " + event.getType()
        );

        guardarEventoProcesado(eventoId, "notificacion.uso", recursoId, rawPayload, "EXITOSO");
        log.info("Evento notificacion.uso procesado exitosamente: recurso={}, eventoId={}", recursoId, eventoId);
    }

    /**
     * Procesa el evento 'reserva.finalizada' (M3 -> M1).
     * Transiciona el recurso a DISPONIBLE.
     */
    @Transactional
    public void procesarReservaFinalizada(ResourceEventPayload<EndedReservationData> event, String rawPayload) {
        UUID eventoId = parseUUID(event.getEventId(), "eventId");
        EndedReservationData data = event.getData();
        if (data == null || data.getResourceId() == null) {
            throw new IllegalArgumentException("El evento reserva.finalizada carece de datos del recurso.");
        }

        UUID recursoId = data.getResourceId();
        CategoriaRecurso categoria = resolverCategoria(data.getResourceCategory(), recursoId);
        LocalDateTime fechaEvento = data.getRealCheckoutDate() != null
                ? data.getRealCheckoutDate().toLocalDateTime()
                : (event.getTimestamp() != null ? event.getTimestamp().toLocalDateTime() : LocalDateTime.now());
        String usuarioResponsable = data.getIdCheckoutM3() != null ? "CHECKOUT:" + data.getIdCheckoutM3() : "MODULO_3";

        EstadoDisponibilidadRecurso estadoAnteriorEnum;
        UUID nuevoEstadoDispId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.DISPONIBLE);

        if (categoria == CategoriaRecurso.ACTIVO) {
            Activo activo = activoRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento reserva.finalizada duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(activo.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.DISPONIBLE);

            activo.setDisponibilidadId(nuevoEstadoDispId);

            if (data.getPhysicalState() != null && !data.getPhysicalState().isBlank()) {
                EstadoFisicoActivo estadoFisicoEnum = parseEstadoFisico(data.getPhysicalState());
                UUID estadoFisicoId = obtenerEstadoFisicoId(estadoFisicoEnum);
                activo.setEstadoFisicoId(estadoFisicoId);
            }

            activoRepo.save(activo);
        } else {
            Espacio espacio = espacioRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento reserva.finalizada duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(espacio.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.DISPONIBLE);

            espacio.setDisponibilidadId(nuevoEstadoDispId);
            espacioRepo.save(espacio);
        }

        registrarHistorial(
                recursoId,
                estadoAnteriorEnum.name(),
                EstadoDisponibilidadRecurso.DISPONIBLE.name(),
                usuarioResponsable,
                fechaEvento,
                "Check-out exitoso / Desocupación - Tipo: " + event.getType()
        );

        guardarEventoProcesado(eventoId, "reserva.finalizada", recursoId, rawPayload, "EXITOSO");
        log.info("Evento reserva.finalizada procesado exitosamente: recurso={}, eventoId={}", recursoId, eventoId);
    }

    /**
     * Procesa el evento 'reporte.danos' (M3 -> M1).
     * Transiciona el recurso a EN_MANTENIMIENTO y si es activo actualiza el estado físico a MAL_ESTADO o AVERIADO.
     */
    @Transactional
    public void procesarReporteDanos(ResourceEventPayload<DamageReportData> event, String rawPayload) {
        UUID eventoId = parseUUID(event.getEventId(), "eventoId");
        DamageReportData data = event.getData();
        if (data == null || data.getResourceId() == null) {
            throw new IllegalArgumentException("El evento reporte.danos carece de datos del recurso.");
        }

        UUID recursoId = data.getResourceId();
        CategoriaRecurso categoria = resolverCategoria(data.getResourceCategory(), recursoId);
        LocalDateTime fechaEvento = event.getTimestamp() != null ? event.getTimestamp().toLocalDateTime() : LocalDateTime.now();
        String usuarioResponsable = (data.getStudent() != null && data.getStudent().getMail() != null)
                ? data.getStudent().getMail()
                : (data.getIdNovedadM3() != null ? "NOVEDAD:" + data.getIdNovedadM3() : "MODULO_3");

        EstadoDisponibilidadRecurso estadoAnteriorEnum;
        UUID mantenimientoDispId = obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso.EN_MANTENIMIENTO);

        if (categoria == CategoriaRecurso.ACTIVO) {
            Activo activo = activoRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento reporte.danos duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(activo.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.EN_MANTENIMIENTO);

            activo.setDisponibilidadId(mantenimientoDispId);

            EstadoFisicoActivo estadoFisicoEnum = parseEstadoFisico(data.getPhysicalState());
            UUID estadoFisicoId = obtenerEstadoFisicoId(estadoFisicoEnum);
            activo.setEstadoFisicoId(estadoFisicoId);

            activoRepo.save(activo);
        } else {
            Espacio espacio = espacioRepo.findByIdForUpdate(recursoId)
                    .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado con ID: " + recursoId));

            if (esDuplicado(eventoId)) {
                log.warn("Evento reporte.danos duplicado ignorado: {}", eventoId);
                return;
            }

            estadoAnteriorEnum = obtenerEstadoDisponibilidadEnum(espacio.getDisponibilidadId());

            validarTransicion(estadoAnteriorEnum, EstadoDisponibilidadRecurso.EN_MANTENIMIENTO);

            espacio.setDisponibilidadId(mantenimientoDispId);
            espacioRepo.save(espacio);
        }

        String motivo = (data.getDescription() != null && !data.getDescription().isBlank())
                ? "Novedad técnica/daño: " + data.getDescription()
                : "Reporte de daños desde Módulo 3";

        registrarHistorial(
                recursoId,
                estadoAnteriorEnum.name(),
                EstadoDisponibilidadRecurso.EN_MANTENIMIENTO.name(),
                usuarioResponsable,
                fechaEvento,
                motivo
        );

        guardarEventoProcesado(eventoId, "reporte.danos", recursoId, rawPayload, "EXITOSO");
        log.info("Evento reporte.danos procesado exitosamente: recurso={}, eventoId={}", recursoId, eventoId);
    }

    private boolean esDuplicado(UUID eventoId) {
        return eventosProcesadosRepo.findByIdForUpdate(eventoId).isPresent();
    }

    private void validarTransicion(EstadoDisponibilidadRecurso actual, EstadoDisponibilidadRecurso nuevo) {
        if (actual == nuevo) {
            log.info("El recurso ya se encuentra en el estado {}; no hay cambio necesario.", actual);
            return;
        }
        RecursoStateMachine.validarTransicion(actual, nuevo);
    }

    private CategoriaRecurso resolverCategoria(String categoryStr, UUID recursoId) {
        if (categoryStr != null) {
            try {
                return CategoriaRecurso.valueOf(categoryStr.toUpperCase().trim());
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (activoRepo.existsById(recursoId)) {
            return CategoriaRecurso.ACTIVO;
        }
        if (espacioRepo.existsById(recursoId)) {
            return CategoriaRecurso.ESPACIO;
        }
        throw new IllegalArgumentException("No existe recurso registrado con ID: " + recursoId);
    }

    private EstadoFisicoActivo parseEstadoFisico(String rawState) {
        if (rawState == null || rawState.isBlank()) {
            return EstadoFisicoActivo.MAL_ESTADO;
        }
        String clean = rawState.trim().toUpperCase();
        if (clean.contains("DAÑ") || clean.contains("DAN") || clean.equals("AVERIADO")) {
            return EstadoFisicoActivo.AVERIADO;
        }
        if (clean.contains("MAL") || clean.contains("DEFECTUOSO")) {
            return EstadoFisicoActivo.MAL_ESTADO;
        }
        if (clean.equals("OPTIMO") || clean.equals("ÓPTIMO") || clean.equals("BUENO")) {
            return EstadoFisicoActivo.OPTIMO;
        }
        if (clean.equals("NUEVO")) {
            return EstadoFisicoActivo.NUEVO;
        }
        try {
            return EstadoFisicoActivo.valueOf(clean);
        } catch (IllegalArgumentException ex) {
            // Un valor no reconocido es un mensaje venenoso: no se debe degradar
            // silenciosamente el activo. El DefaultErrorHandler lo enviará a la DLT.
            throw new IllegalArgumentException(
                    "Estado físico no reconocido en el evento: '" + rawState + "'. Valores permitidos: "
                            + java.util.Arrays.toString(EstadoFisicoActivo.values()));
        }
    }

    private UUID obtenerEstadoDisponibilidadId(EstadoDisponibilidadRecurso estadoEnum) {
        return estadoDisponibilidadRepo.findByDisponibilidadRecurso(estadoEnum)
                .map(EstadoDisponibilidad::getId)
                .orElseGet(() -> {
                    EstadoDisponibilidad nuevo = EstadoDisponibilidad.builder()
                            .disponibilidadRecurso(estadoEnum)
                            .descripcion(estadoEnum.name())
                            .build();
                    return estadoDisponibilidadRepo.save(nuevo).getId();
                });
    }

    private EstadoDisponibilidadRecurso obtenerEstadoDisponibilidadEnum(UUID id) {
        if (id == null) {
            throw new IllegalStateException(
                    "El recurso no tiene un estado de disponibilidad asociado en la base de datos.");
        }
        return estadoDisponibilidadRepo.findById(id)
                .map(EstadoDisponibilidad::getDisponibilidadRecurso)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe el estado de disponibilidad con id " + id
                                + "; los datos del recurso están corruptos."));
    }

    private UUID obtenerEstadoFisicoId(EstadoFisicoActivo estadoEnum) {
        return estadoActivoRepo.findByEstado(estadoEnum)
                .map(EstadoActivo::getId)
                .orElseGet(() -> {
                    EstadoActivo nuevo = EstadoActivo.builder()
                            .estado(estadoEnum)
                            .descripcion(estadoEnum.name())
                            .build();
                    return estadoActivoRepo.save(nuevo).getId();
                });
    }

    private void registrarHistorial(
            UUID idRecurso,
            String estadoAnterior,
            String estadoNuevo,
            String usuarioResponsable,
            LocalDateTime fechaEvento,
            String motivo) {

        HistorialRecurso historial = HistorialRecurso.builder()
                .idRecurso(idRecurso)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .usuarioResponsable(usuarioResponsable)
                .fechaEvento(fechaEvento)
                .motivo(motivo)
                .build();

        historialRecursoRepo.save(historial);
    }

    private void guardarEventoProcesado(
            UUID eventoId,
            String tipoEvento,
            UUID recursoId,
            String rawPayload,
            String resultado) {

        EventosProcesados eventoProcesado = EventosProcesados.builder()
                .eventoId(eventoId)
                .tipoEvento(tipoEvento)
                .recursoId(recursoId)
                .payload(rawPayload)
                .resultado(resultado)
                .fechaProcesamiento(LocalDateTime.now())
                .build();

        eventosProcesadosRepo.save(eventoProcesado);
    }

    private UUID parseUUID(String str, String fieldName) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' no puede ser nulo o vacío.");
        }
        String clean = str.trim();
        if (clean.startsWith("evt-")) {
            clean = clean.substring(4);
        }
        try {
            return UUID.fromString(clean);
        } catch (IllegalArgumentException ex) {
            // No se debe inventar un identificador: un eventoId inválido impide
            // garantizar idempotencia y debe tratarse como mensaje venenoso (DLT).
            throw new IllegalArgumentException(
                    "El campo '" + fieldName + "' no es un UUID válido: '" + str + "'.", ex);
        }
    }
}
