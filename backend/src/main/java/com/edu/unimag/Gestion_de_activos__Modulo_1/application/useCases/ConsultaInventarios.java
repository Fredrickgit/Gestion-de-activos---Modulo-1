package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultaInventarios {

    private final EspacioRepository espacioRepository;
    private final ActivoRepository activoRepository;

    public ConsultaInventarios(EspacioRepository espacioRepository, ActivoRepository activoRepository) {
        this.espacioRepository = espacioRepository;
        this.activoRepository = activoRepository;
    }

    public Page<Espacio> buscarEspaciosPorNombre(String nombre, int numeroPagina) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El criterio de búsqueda no puede estar vacío");
        }

        PageRequest pageRequest = PageRequest.of(numeroPagina, 20);
        Page<Espacio> resultados = espacioRepository.findByNombreContainingIgnoreCase(nombre, pageRequest);

        if (resultados.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda"); 
        }

        return resultados;
    }

    public Page<Activo> buscarActivosPorCategoria(CategoriaRecurso categoria, int numeroPagina) {
        List<Activo> activos = activoRepository.findByCategoria(categoria);
        if (activos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda"); 
        }

        return paginarLista(activos, numeroPagina);
    }

    public Page<Espacio> buscarEspaciosPorFacultad(UUID facultadId, int numeroPagina) {
        List<Espacio> espacios = espacioRepository.findByFacultadId(facultadId);
        if (espacios.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda");
        }

        return paginarLista(espacios, numeroPagina);
    }

    public Page<Espacio> buscarEspaciosPorAforoMinimo(int aforoMinimo, int numeroPagina) {
        List<Espacio> espacios = espacioRepository.findByAforoGreaterThanEqual(aforoMinimo);
        if (espacios.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron resultados para los criterios de búsqueda");
        }

        return paginarLista(espacios, numeroPagina);
    }

    private <T> Page<T> paginarLista(List<T> lista, int numeroPagina) {
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20);
        int inicio = (int) pageRequest.getOffset();
        int fin = Math.min((inicio + pageRequest.getPageSize()), lista.size());

        if (inicio >= lista.size()) {
            throw new IllegalArgumentException("No hay más registros");
        }

        return new PageImpl<>(lista.subList(inicio, fin), pageRequest, lista.size());
    }
}