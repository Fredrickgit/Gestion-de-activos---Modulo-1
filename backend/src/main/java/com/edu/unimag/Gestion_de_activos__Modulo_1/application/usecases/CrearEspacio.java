package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioInput;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioOutput;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class CrearEspacio {

    private final EspacioRepository espacioRepository;
    private final FacultadRepository facultadRepository;
    private final UbicacionUniversitariaRepository ubicacionUniversitariaRepository;

    public CrearEspacio(EspacioRepository espacioRepository,
                        FacultadRepository facultadRepository,
                        UbicacionUniversitariaRepository ubicacionUniversitariaRepository) {
        this.espacioRepository = espacioRepository;
        this.facultadRepository = facultadRepository;
        this.ubicacionUniversitariaRepository = ubicacionUniversitariaRepository;
    }

    @Transactional
    public CrearEspacioOutput ejecutar(CrearEspacioInput input) {
        validarCamposObligatorios(input);

        if (espacioRepository.existsByNombre(input.nombre())) {
            throw new DomainException("Ya existe un espacio registrado con el mismo nombre");
        }
        if (input.aforo() <= 0) {
            throw new DomainException("El aforo máximo debe ser un número entero positivo mayor a cero");
        }
        if (!facultadRepository.existsById(input.facultadId())) {
            throw new DomainException("La facultad especificada no existe");
        }
        if (!ubicacionUniversitariaRepository.existsById(input.ubicacionId())) {
            throw new DomainException("La ubicación especificada no existe");
        }

        Espacio espacioNuevo = Espacio.builder()
                .nombre(input.nombre())
                .codigo(input.codigo())
                .aforo(input.aforo())
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(input.plazoDevolucion() != null ? input.plazoDevolucion() : 0)
                .facultadId(input.facultadId())
                .disponibilidadId(java.util.UUID.randomUUID())
                .ubicacionId(input.ubicacionId())
                .build();

        Espacio guardado = espacioRepository.save(espacioNuevo);

        return new CrearEspacioOutput(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCodigo(),
                guardado.getAforo(),
                guardado.getCategoria(),
                guardado.getPlazoDevolucion(),
                guardado.getFacultadId(),
                guardado.getDisponibilidadId(),
                guardado.getUbicacionId());
    }

    private void validarCamposObligatorios(CrearEspacioInput input) {
        if (Objects.isNull(input) || Objects.isNull(input.nombre()) || input.nombre().isBlank()
                || Objects.isNull(input.codigo()) || input.codigo().isBlank()) {
            throw new DomainException("Todos los atributos obligatorios del espacio deben ser completados");
        }
    }
}
