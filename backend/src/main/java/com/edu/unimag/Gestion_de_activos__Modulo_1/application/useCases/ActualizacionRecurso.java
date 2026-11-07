package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActualizacionRecurso {

    private final ActivoRepository activoRepository;

    public ActualizacionRecurso(ActivoRepository activoRepository) {
        this.activoRepository = activoRepository;
    }

    @Transactional
    public Activo actualizarEstadoActivo(UUID activoId, UUID nuevaDisponibilidadId) {
        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        

        if (activo.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new IllegalArgumentException("El recurso ya se encuentra en el estado seleccionado.");
        }
        
        activo.setDisponibilidadId(nuevaDisponibilidadId);
        return activoRepository.save(activo);
    }
}