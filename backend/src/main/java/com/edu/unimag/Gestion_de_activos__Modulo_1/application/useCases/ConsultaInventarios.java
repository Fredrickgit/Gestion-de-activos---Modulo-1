package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ConsultaInventarios {

    private final EspacioRepository espacioRepository;

    public ConsultaInventarios(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    // SPEC 2: Búsqueda de recursos en inventario por atributo (nombre)[cite: 12]
    public Page<Espacio> buscarEspaciosPorNombre(String nombre, int numeroPagina) {
        // Implementación de paginación de máximo 20 recursos por página[cite: 12]
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20);[cite: 12]
        
        Page<Espacio> resultados = espacioRepository.findByNombreContainingIgnoreCase(nombre, pageRequest);
        
        // Manejo de inventario sin resultados[cite: 12]
        if (resultados.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda");[cite: 12]
        }
        
        return resultados;
    }
}git