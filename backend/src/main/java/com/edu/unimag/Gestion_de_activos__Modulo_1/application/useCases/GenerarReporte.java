package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GenerarReporte {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public GenerarReporte(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    public Page<Activo> previsualizarReporteActivos(int numeroPagina, String ordenarPor) {
        String campoOrden = (ordenarPor != null && !ordenarPor.isBlank()) ? ordenarPor : "nombre";

        PageRequest pageRequest = PageRequest.of(numeroPagina, 20,
            Sort.by(Sort.Direction.ASC, campoOrden).and(Sort.by(Sort.Direction.ASC, "id"))); 

        Page<Activo> resultados = activoRepository.findAll(pageRequest);

        if (resultados.isEmpty() && numeroPagina > 0) {
            throw new IllegalArgumentException("No hay más registros"); 
        }

        return resultados;
    }

    public Page<Activo> previsualizarReporteActivosPorEstado(UUID disponibilidadId, int numeroPagina) {
        List<Activo> activos = activoRepository.findByDisponibilidadId(disponibilidadId);

        if (activos.isEmpty()) {
            throw new IllegalArgumentException("No hay recursos que coincidan con los filtros"); 
        }

        return paginarLista(activos, numeroPagina);
    }

    public Page<Espacio> previsualizarReporteEspaciosPorEstado(UUID disponibilidadId, int numeroPagina) {
        List<Espacio> espacios = espacioRepository.findByDisponibilidadId(disponibilidadId);

        if (espacios.isEmpty()) {
            throw new IllegalArgumentException("No hay recursos que coincidan con los filtros");
        }

        return paginarLista(espacios, numeroPagina);
    }

    private <T> Page<T> paginarLista(List<T> lista, int numeroPagina) {
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20, Sort.by(Sort.Direction.ASC, "id")); 
        int inicio = (int) pageRequest.getOffset();
        int fin = Math.min((inicio + pageRequest.getPageSize()), lista.size());

        if (inicio >= lista.size()) {
            throw new IllegalArgumentException("No hay más registros"); 
        }

        return new PageImpl<>(lista.subList(inicio, fin), pageRequest, lista.size());
    }
}