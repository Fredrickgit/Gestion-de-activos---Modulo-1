package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RecursoService {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public RecursoService(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    @Transactional
    public Activo crearActivo(Activo activo, UUID disponibilidadDisponibleId) {
        if (activoRepository.existsById(activo.getId())) {
            throw new IllegalArgumentException("El identificador del activo ya se encuentra registrado");
        }
        // Asignación de estado inicial 'Disponible' mediante el ID referenciado
        activo.setDisponibilidadId(disponibilidadDisponibleId);
        return activoRepository.save(activo); // Persistencia transaccional
    }

    // SPEC 1: Creación de un espacio
    @Transactional
    public Espacio crearEspacio(Espacio espacio, UUID disponibilidadDisponibleId) {
        if (espacioRepository.existsById(espacio.getId())) {
            throw new IllegalArgumentException("El identificador del espacio ya se encuentra registrado"); 
        }
        if (espacio.getAforo() <= 0) {
            throw new IllegalArgumentException("El aforo máximo debe ser un número entero positivo mayor a cero"); 
        }
        espacio.setDisponibilidadId(disponibilidadDisponibleId);
        return espacioRepository.save(espacio);
    }

    // SPEC 3: Actualizar el estado del recurso
    @Transactional
    public Activo actualizarEstadoActivo(UUID activoId, UUID nuevaDisponibilidadId) {
        Activo activo = activoRepository.findById(activoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        
        if (activo.getDisponibilidadId().equals(nuevaDisponibilidadId)) {
            throw new IllegalArgumentException("El recurso ya se encuentra en el estado seleccionado."); 
        }
        
        // Aquí se invocaría el servicio de auditoría (externo a las entidades dadas) para registrar el cambio.
        activo.setDisponibilidadId(nuevaDisponibilidadId);
        return activoRepository.save(activo);
    }
}