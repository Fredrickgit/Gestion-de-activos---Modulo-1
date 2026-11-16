package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoInput;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoOutput;
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
public class CrearActivo {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoActivoRepository estadoActivoRepository;
    private final FacultadRepository facultadRepository;
    private final UbicacionUniversitariaRepository ubicacionUniversitariaRepository;

    public CrearActivo(ActivoRepository activoRepository,
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
    public CrearActivoOutput ejecutar(CrearActivoInput input) {
        validarCamposObligatoriosActivo(input);
        UUID estadoDisponibleId = UUID.randomUUID();

        if (input.espacioId() != null && !espacioRepository.existsById(input.espacioId())) {
            throw new DomainException("El espacio de ubicación especificado no existe");
        }
        if (!estadoActivoRepository.existsById(input.estadoFisicoId())) {
            throw new DomainException("El estado físico especificado no existe");
        }
        if (activoRepository.existsByCodigo(input.codigo())) {
            throw new DomainException("La placa de inventario/código ya se encuentra registrada");
        }

        Activo activoNuevo = Activo.builder()
                .nombre(input.nombre())
                .codigo(input.codigo())
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(input.estadoFisicoId())
                .disponibilidadId(estadoDisponibleId)
                .espacioId(input.espacioId())
                .tipoActivoId(input.tipoActivoId())
                .build();

        Activo guardado = activoRepository.save(activoNuevo);

        return new CrearActivoOutput(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCodigo(),
                guardado.getCategoria(),
                guardado.getEstadoFisicoId(),
                guardado.getDisponibilidadId(),
                guardado.getEspacioId(),
                guardado.getTipoActivoId());
    }

    private void validarCamposObligatoriosActivo(CrearActivoInput input) {
        if (Objects.isNull(input) || Objects.isNull(input.nombre()) || input.nombre().isBlank()
                || Objects.isNull(input.codigo()) || input.codigo().isBlank()
                || Objects.isNull(input.estadoFisicoId()) || Objects.isNull(input.tipoActivoId())) {
            throw new DomainException("Todos los atributos obligatorios del activo deben ser completados");
        }
    }
}
