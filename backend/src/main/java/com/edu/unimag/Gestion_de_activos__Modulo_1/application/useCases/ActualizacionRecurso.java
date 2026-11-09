package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EstadoDisponibilidadRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado")); 

        if (activo.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new IllegalArgumentException("El recurso ya se encuentra en el estado seleccionado."); 
        }

        activo.setDisponibilidadId(nuevaDisponibilidadId);
        return activoRepository.save(activo);
    }

    @Transactional
    public Espacio actualizarEstadoDisponibilidadEspacio(UUID espacioId, UUID nuevaDisponibilidadId) {
        validarEstadoDisponibilidad(nuevaDisponibilidadId);

        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));

        if (espacio.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new IllegalArgumentException("El recurso ya se encuentra en el estado seleccionado."); 
        }

        espacio.setDisponibilidadId(nuevaDisponibilidadId); 
        return espacioRepository.save(espacio);
    }

    @Transactional
    public Activo actualizarEstadoFisicoActivo(UUID activoId, EstadoFisicoActivo nuevoEstadoFisico) {
        UUID nuevoEstadoFisicoId = estadoActivoRepository.findByEstado(nuevoEstadoFisico)
                .orElseThrow(() -> new IllegalArgumentException("El estado físico especificado no existe"))
                .getId();

        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));

        if (activo.getEstadoFisicoId().equals(nuevoEstadoFisicoId)) {
            throw new IllegalArgumentException("El recurso ya se encuentra en el estado físico seleccionado.");
        }

        activo.setEstadoFisicoId(nuevoEstadoFisicoId);
        return activoRepository.save(activo);
    }

    private void validarEstadoDisponibilidad(UUID disponibilidadId) {
        if (!estadoDisponibilidadRepository.existsById(disponibilidadId)) {
            throw new IllegalArgumentException("El estado de disponibilidad especificado no existe");
        }
    }
}