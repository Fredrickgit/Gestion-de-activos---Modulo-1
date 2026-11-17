package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CatalogoItemView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CatalogosView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.TipoActivoCatalogoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.TipoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.UbicacionUniversitariaRepository;

/**
 * Expone los catálogos maestros que el frontend necesita para construir
 * formularios (facultades, ubicaciones, tipos de activo y estados).
 */
public class ConsultarCatalogos {

    private final FacultadRepository facultadRepository;
    private final UbicacionUniversitariaRepository ubicacionRepository;
    private final TipoActivoRepository tipoActivoRepository;
    private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;
    private final EstadoActivoRepository estadoActivoRepository;

    public ConsultarCatalogos(FacultadRepository facultadRepository,
                              UbicacionUniversitariaRepository ubicacionRepository,
                              TipoActivoRepository tipoActivoRepository,
                              EstadoDisponibilidadRepository estadoDisponibilidadRepository,
                              EstadoActivoRepository estadoActivoRepository) {
        this.facultadRepository = facultadRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.tipoActivoRepository = tipoActivoRepository;
        this.estadoDisponibilidadRepository = estadoDisponibilidadRepository;
        this.estadoActivoRepository = estadoActivoRepository;
    }

    public CatalogosView obtener() {
        return new CatalogosView(
                facultadRepository.findAll().stream().map(this::toItem).toList(),
                ubicacionRepository.findAll().stream().map(this::toItem).toList(),
                tipoActivoRepository.findAll().stream().map(this::toTipo).toList(),
                estadoDisponibilidadRepository.findAll().stream().map(this::toItem).toList(),
                estadoActivoRepository.findAll().stream().map(this::toItem).toList());
    }

    private CatalogoItemView toItem(Facultad facultad) {
        return new CatalogoItemView(facultad.getId(), facultad.getNombre(), facultad.getDescripcion());
    }

    private CatalogoItemView toItem(UbicacionUniversitaria ubicacion) {
        return new CatalogoItemView(ubicacion.getId(), ubicacion.getNombre(), ubicacion.getDescripcion());
    }

    private TipoActivoCatalogoView toTipo(TipoActivo tipo) {
        return new TipoActivoCatalogoView(
                tipo.getId(), tipo.getNombre(), tipo.getDescripcion(), tipo.getPlazoDevolucion());
    }

    private CatalogoItemView toItem(EstadoDisponibilidad estado) {
        return new CatalogoItemView(
                estado.getId(), estado.getDisponibilidadRecurso().name(), estado.getDescripcion());
    }

    private CatalogoItemView toItem(EstadoActivo estado) {
        return new CatalogoItemView(estado.getId(), estado.getEstado().name(), estado.getDescripcion());
    }
}
