package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreacionRecurso {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public CreacionRecurso(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    // SPEC 1: Creación de activo validando identificador único[cite: 11]
    @Transactional
    public Activo crearActivo(Activo activo, UUID estadoDisponibleId) {
        if (activoRepository.existsById(activo.getId())) {
            throw new IllegalArgumentException("El identificador ya se encuentra registrado"); // Bloqueo por ID duplicado[cite: 11]
        }
        activo.setDisponibilidadId(estadoDisponibleId); // Asignación automática de estado inicial[cite: 11]
        return activoRepository.save(activo);
    }

    // SPEC 1: Creación de espacio validando aforo y unicidad[cite: 11]
    @Transactional
    public Espacio crearEspacio(Espacio espacio, UUID estadoDisponibleId) {
        if (espacioRepository.existsById(espacio.getId())) {
            throw new IllegalArgumentException("El identificador ya se encuentra registrado");[cite: 11]
        }
        if (espacio.getAforo() <= 0) {
            throw new IllegalArgumentException("El aforo máximo debe ser un número entero positivo mayor a cero");[cite: 11]
        }
        espacio.setDisponibilidadId(estadoDisponibleId);[cite: 11]
        return espacioRepository.save(espacio);
    }
}