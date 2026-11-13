package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class ActualizarDisponibilidadEspacio {

    private final EspacioRepository espacioRepository;
    private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    public ActualizarDisponibilidadEspacio(EspacioRepository espacioRepository,
                                           EstadoDisponibilidadRepository estadoDisponibilidadRepository) {
        this.espacioRepository = espacioRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
    }

    @Transactional
    public Espacio ejecutar(UUID espacioId, UUID nuevaDisponibilidadId) {
        if (!estadoDisponibilidadRepository.existsById(nuevaDisponibilidadId)) {
            throw new DomainException("El estado de disponibilidad especificado no existe");
        }

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
}
