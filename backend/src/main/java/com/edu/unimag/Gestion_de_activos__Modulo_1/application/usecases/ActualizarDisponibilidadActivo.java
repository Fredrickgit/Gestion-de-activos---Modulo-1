package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class ActualizarDisponibilidadActivo {

    private final ActivoRepository activoRepository;
    private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    public ActualizarDisponibilidadActivo(ActivoRepository activoRepository,
                                         EstadoDisponibilidadRepository estadoDisponibilidadRepository) {
        this.activoRepository = activoRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
    }

    @Transactional
    public Activo ejecutar(UUID activoId, UUID nuevaDisponibilidadId) {
        if (!estadoDisponibilidadRepository.existsById(nuevaDisponibilidadId)) {
            throw new DomainException("El estado de disponibilidad especificado no existe");
        }

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
}
