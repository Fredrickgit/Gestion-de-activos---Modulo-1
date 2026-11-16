package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;
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
            throw new DomainException("El identificador ya se encuentra registrado");
        }
        if (activoRepository.existsByCodigo(activo.getCodigo())) {
            throw new DomainException("La placa de inventario/código ya se encuentra registrada");
        }
        if (activo.getEspacioId() != null && !espacioRepository.existsById(activo.getEspacioId())) {
            throw new DomainException("El espacio de ubicación especificado no existe");
        }
        if (!estadoActivoRepository.existsById(activo.getEstadoFisicoId())) {
            throw new DomainException("El estado físico especificado no existe");
        }

        Activo activoNuevo = Activo.builder()
                .id(activo.getId())
                .nombre(activo.getNombre())
                .codigo(activo.getCodigo())
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(activo.getEstadoFisicoId())
                .disponibilidadId(estadoDisponibleId)
                .espacioId(activo.getEspacioId())
                .tipoActivoId(activo.getTipoActivoId())
                .build();

        return activoRepository.save(activoNuevo);
    }

    @Transactional
    public Espacio crearEspacio(Espacio espacio, UUID estadoDisponibleId) {
        validarCamposObligatoriosEspacio(espacio);

        if (espacio.getId() != null && espacioRepository.existsById(espacio.getId())) {
            throw new DomainException("El identificador ya se encuentra registrado");
        }
        if (espacioRepository.existsByNombre(espacio.getNombre())) {
            throw new DomainException("Ya existe un espacio registrado con el mismo nombre");
        }
        if (espacio.getAforo() <= 0) {
            throw new DomainException("El aforo máximo debe ser un número entero positivo mayor a cero");
        }
        if (!facultadRepository.existsById(espacio.getFacultadId())) {
            throw new DomainException("La facultad especificada no existe");
        }
        if (!ubicacionUniversitariaRepository.existsById(espacio.getUbicacionId())) {
            throw new DomainException("La ubicación especificada no existe");
        }

        Espacio espacioNuevo = Espacio.builder()
                .id(espacio.getId())
                .nombre(espacio.getNombre())
                .codigo(espacio.getCodigo())
                .aforo(espacio.getAforo())
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(espacio.getPlazoDevolucion())
                .facultadId(espacio.getFacultadId())
                .disponibilidadId(estadoDisponibleId)
                .ubicacionId(espacio.getUbicacionId())
                .build();

        return espacioRepository.save(espacioNuevo);
    }

    private void validarCamposObligatoriosActivo(Activo activo) {
        if (Objects.isNull(activo) ||
            Objects.isNull(activo.getNombre()) || activo.getNombre().isBlank() ||
            Objects.isNull(activo.getCodigo()) || activo.getCodigo().isBlank() ||
            Objects.isNull(activo.getEspacioId()) ||
            Objects.isNull(activo.getEstadoFisicoId()) ||
            Objects.isNull(activo.getTipoActivoId())) {
            throw new DomainException("Todos los atributos obligatorios del activo deben ser completados");
        }
    }

    private void validarCamposObligatoriosEspacio(Espacio espacio) {
        if (Objects.isNull(espacio) ||
            Objects.isNull(espacio.getNombre()) || espacio.getNombre().isBlank() ||
            Objects.isNull(espacio.getCodigo()) || espacio.getCodigo().isBlank() ||
            Objects.isNull(espacio.getFacultadId()) ||
            Objects.isNull(espacio.getUbicacionId())) {
            throw new DomainException("Todos los atributos obligatorios del espacio deben ser completados");
        }
    }
}