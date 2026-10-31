package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultaService {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public ConsultaService(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    // SPEC 0: Consultar disponibilidad específica[cite: 10]
    public UUID consultarDisponibilidadActivo(UUID activoId) {
        return activoRepository.findById(activoId)
                .map(Activo::getDisponibilidadId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado")); //[cite: 10]
    }

    // SPEC 2 & 4: Consultar Inventario y Generar Reportes con paginación y ordenamiento[cite: 12, 14]
    public Page<Activo> listarActivosPaginados(int numeroPagina, String ordenarPor) {
        // Paginación estricta de 20 elementos y ordenamiento estable usando ID como segundo criterio[cite: 12, 14]
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20, 
            Sort.by(Sort.Direction.ASC, ordenarPor).and(Sort.by(Sort.Direction.ASC, "id"))); 
        
        Page<Activo> resultados = activoRepository.findAll(pageRequest);
        if (resultados.isEmpty() && numeroPagina > 0) {
            // Manejo de páginas inexistentes redirigiendo visualmente en el frontend o devolviendo la última aquí[cite: 14]
            throw new IllegalArgumentException("No hay más registros");
        }
        return resultados;
    }

    public Page<Espacio> buscarEspaciosPorNombre(String nombre, int numeroPagina) {
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20); //[cite: 12]
        Page<Espacio> resultados = espacioRepository.findByNombreContainingIgnoreCase(nombre, pageRequest);
        if (resultados.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda"); //[cite: 12]
        }
        return resultados;
    }
}