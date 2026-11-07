package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GenerarReporte {

    private final ActivoRepository activoRepository;

    public GenerarReporte(ActivoRepository activoRepository) {
        this.activoRepository = activoRepository;
    }

    public Page<Activo> previsualizarReporteActivos(int numeroPagina, String ordenarPor) {

        PageRequest pageRequest = PageRequest.of(numeroPagina, 20, 
            Sort.by(Sort.Direction.ASC, ordenarPor).and(Sort.by(Sort.Direction.ASC, "id")));
        
        Page<Activo> resultados = activoRepository.findAll(pageRequest);
        
        // Manejo de navegación fuera de límites
        if (resultados.isEmpty() && numeroPagina > 0) {
            throw new IllegalArgumentException("No hay más registros");
        }
        
        return resultados;
    }
}