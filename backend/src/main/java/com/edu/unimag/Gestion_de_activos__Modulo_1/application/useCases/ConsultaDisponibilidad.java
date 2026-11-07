package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultaDisponibilidad {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public ConsultaDisponibilidad(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    public UUID consultarDisponibilidadActivo(UUID activoId) {
        return activoRepository.findById(activoId)
                .map(Activo::getDisponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado")); 
    }

    public UUID consultarDisponibilidadEspacio(UUID espacioId) {
        return espacioRepository.findById(espacioId)
                .map(Espacio::getDisponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado")); 
    }
}