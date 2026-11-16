package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class ActualizarEstadoFisicoActivo {

    private final ActivoRepository activoRepository;
    private final EstadoActivoRepository estadoActivoRepository;

    public ActualizarEstadoFisicoActivo(ActivoRepository activoRepository,
                                       EstadoActivoRepository estadoActivoRepository) {
        this.activoRepository = activoRepository;
        this.estadoActivoRepository = estadoActivoRepository;
    }

    @Transactional
    public Activo ejecutar(UUID activoId, EstadoFisicoActivo nuevoEstadoFisico) {
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
}
