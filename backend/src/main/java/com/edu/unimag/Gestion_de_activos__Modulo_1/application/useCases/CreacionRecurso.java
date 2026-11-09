package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class CreacionRecurso {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoActivoRepository estadoActivoRepository;
    private final FacultadRepository facultadRepository;
    private final UbicacionUniversitariaRepository ubicacionUniversitariaRepository;

    public CreacionRecurso(ActivoRepository activoRepository,
                           EspacioRepository espacioRepository,
                           EstadoActivoRepository estadoActivoRepository,
                           FacultadRepository facultadRepository,
                           UbicacionUniversitariaRepository ubicacionUniversitariaRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
        this.estadoActivoRepository = estadoActivoRepository;
        this.facultadRepository = facultadRepository;
        this.ubicacionUniversitariaRepository = ubicacionUniversitariaRepository;
    }

    @Transactional
    public Activo crearActivo(Activo activo, UUID estadoDisponibleId) {
        validarCamposObligatoriosActivo(activo);

        if (activo.getId() != null && activoRepository.existsById(activo.getId())) {
            throw new IllegalArgumentException("El identificador ya se encuentra registrado"); 
        }
        if (activoRepository.existsByCodigo(String.valueOf(activo.getCodigo()))) {
            throw new IllegalArgumentException("La placa de inventario/código ya se encuentra registrada"); 
        }
        if (!espacioRepository.existsById(activo.getEspacioId())) {
            throw new IllegalArgumentException("El espacio de ubicación especificado no existe"); 
        }
        if (!estadoActivoRepository.existsById(activo.getEstadoFisicoId())) {
            throw new IllegalArgumentException("El estado físico especificado no existe"); 
        }

        activo.setCategoria(CategoriaRecurso.ACTIVO); 
        activo.setDisponibilidadId(estadoDisponibleId); 
        return activoRepository.save(activo);
    }

    @Transactional
    public Espacio crearEspacio(Espacio espacio, UUID estadoDisponibleId) {
        validarCamposObligatoriosEspacio(espacio);

        if (espacio.getId() != null && espacioRepository.existsById(espacio.getId())) {
            throw new IllegalArgumentException("El identificador ya se encuentra registrado"); 
        }
        if (espacioRepository.existsByNombre(espacio.getNombre())) {
            throw new IllegalArgumentException("Ya existe un espacio registrado con el mismo nombre"); 
        }
        if (espacio.getAforo() <= 0) {
            throw new IllegalArgumentException("El aforo máximo debe ser un número entero positivo mayor a cero");
        }
        if (!facultadRepository.existsById(espacio.getFacultadId())) {
            throw new IllegalArgumentException("La facultad especificada no existe"); 
        }
        if (!ubicacionUniversitariaRepository.existsById(espacio.getUbicacionId())) {
            throw new IllegalArgumentException("La ubicación especificada no existe"); 
        }

        espacio.setCategoria(CategoriaRecurso.ESPACIO); 
        espacio.setDisponibilidadId(estadoDisponibleId); 
        return espacioRepository.save(espacio);
    }

    private void validarCamposObligatoriosActivo(Activo activo) {
        if (Objects.isNull(activo.getNombre()) || activo.getNombre().isBlank() ||
            activo.getCodigo() <= 0 ||
            Objects.isNull(activo.getEspacioId()) ||
            Objects.isNull(activo.getEstadoFisicoId())) {
            throw new IllegalArgumentException("Todos los atributos obligatorios del activo deben ser completados");
        }
    }

    private void validarCamposObligatoriosEspacio(Espacio espacio) {
        if (Objects.isNull(espacio.getNombre()) || espacio.getNombre().isBlank() ||
            Objects.isNull(espacio.getFacultadId()) ||
            Objects.isNull(espacio.getUbicacionId())) {
            throw new IllegalArgumentException("Todos los atributos obligatorios del espacio deben ser completados"); 
        }
    }
}