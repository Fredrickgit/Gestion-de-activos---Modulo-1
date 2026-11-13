package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActualizacionRecurso {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;
    private final EstadoActivoRepository estadoActivoRepository;

    public ActualizacionRecurso(ActivoRepository activoRepository,
                                EspacioRepository espacioRepository,
                                EstadoDisponibilidadRepository estadoDisponibilidadRepository,
                                EstadoActivoRepository estadoActivoRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
        this.estadoActivoRepository = estadoActivoRepository;
    }

    @Transactional
    public Activo actualizarEstadoDisponibilidadActivo(UUID activoId, UUID nuevaDisponibilidadId) {
        validarEstadoDisponibilidad(nuevaDisponibilidadId);

        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new DomainException("Recurso no encontrado"));

        if (activo.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new DomainException("El recurso ya se encuentra en el estado seleccionado.");
        }

        Activo activoActualizado = Activo.builder()
                .id(activo.getId())
                .nombre(activo.getNombre())
                .codigo(activo.getCodigo())
                .categoria(activo.getCategoria())
                .estadoFisicoId(activo.getEstadoFisicoId())
                .disponibilidadId(nuevaDisponibilidadId)
                .espacioId(activo.getEspacioId())
                .tipoActivoId(activo.getTipoActivoId())
                .build();

        return activoRepository.save(activoActualizado);
    }

    @Transactional
    public Espacio actualizarEstadoDisponibilidadEspacio(UUID espacioId, UUID nuevaDisponibilidadId) {
        validarEstadoDisponibilidad(nuevaDisponibilidadId);

        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new DomainException("Recurso no encontrado"));

        if (espacio.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new DomainException("El recurso ya se encuentra en el estado seleccionado.");
        }

        Espacio espacioActualizado = Espacio.builder()
                .id(espacio.getId())
                .nombre(espacio.getNombre())
                .codigo(espacio.getCodigo())
                .aforo(espacio.getAforo())
                .categoria(espacio.getCategoria())
                .plazoDevolucion(espacio.getPlazoDevolucion())
                .facultadId(espacio.getFacultadId())
                .disponibilidadId(nuevaDisponibilidadId)
                .ubicacionId(espacio.getUbicacionId())
                .build();

        return espacioRepository.save(espacioActualizado);
    }

    @Transactional
    public Activo actualizarEstadoFisicoActivo(UUID activoId, EstadoFisicoActivo nuevoEstadoFisico) {
        UUID nuevoEstadoFisicoId = estadoActivoRepository.findByEstado(nuevoEstadoFisico)
                .orElseThrow(() -> new DomainException("El estado físico especificado no existe"))
                .getId();

        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new DomainException("Recurso no encontrado"));

        if (activo.getEstadoFisicoId().equals(nuevoEstadoFisicoId)) {
            throw new DomainException("El recurso ya se encuentra en el estado físico seleccionado.");
        }

        Activo activoActualizado = Activo.builder()
                .id(activo.getId())
                .nombre(activo.getNombre())
                .codigo(activo.getCodigo())
                .categoria(activo.getCategoria())
                .estadoFisicoId(nuevoEstadoFisicoId)
                .disponibilidadId(activo.getDisponibilidadId())
                .espacioId(activo.getEspacioId())
                .tipoActivoId(activo.getTipoActivoId())
                .build();

        return activoRepository.save(activoActualizado);
    }

    private void validarEstadoDisponibilidad(UUID disponibilidadId) {
        if (!estadoDisponibilidadRepository.existsById(disponibilidadId)) {
            throw new DomainException("El estado de disponibilidad especificado no existe");
        }
    }
}