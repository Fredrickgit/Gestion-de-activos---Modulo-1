package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultaDisponibilidad {

    private final ActivoRepository activoRepository;
    private final EspacioRepository espacioRepository;

    public ConsultaDisponibilidad(ActivoRepository activoRepository, EspacioRepository espacioRepository) {
        this.activoRepository = activoRepository;
        this.espacioRepository = espacioRepository;
    }

    public UUID consultarDisponibilidadActivo(UUID activoId) {
        return activoRepository.findById(activoId)
                .map(Activo::getDisponibilidadId)
                .orElseThrow(() -> new DomainException("Recurso no encontrado"));
    }

    public UUID consultarDisponibilidadEspacio(UUID espacioId) {
        return espacioRepository.findById(espacioId)
                .map(Espacio::getDisponibilidadId)
                .orElseThrow(() -> new DomainException("Recurso no encontrado"));
    }

    public Page<Activo> consultarDisponibilidadActivosPorCategoria(CategoriaRecurso categoria, int numeroPagina) {
        List<Activo> activos = activoRepository.findByCategoria(categoria);
        if (activos.isEmpty()) {
            throw new DomainException("No se encontraron activos para la categoría especificada");
        }

        return paginarLista(activos, numeroPagina);
    }

    public Page<Espacio> consultarDisponibilidadEspaciosPorCategoria(CategoriaRecurso categoria, int numeroPagina) {
        List<Espacio> espacios = espacioRepository.findByCategoria(categoria);
        if (espacios.isEmpty()) {
            throw new DomainException("No se encontraron espacios para la categoría especificada");
        }

        return paginarLista(espacios, numeroPagina);
    }

    private <T> Page<T> paginarLista(List<T> lista, int numeroPagina) {
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20);
        int inicio = (int) pageRequest.getOffset();
        int fin = Math.min((inicio + pageRequest.getPageSize()), lista.size());

        if (inicio >= lista.size()) {
            throw new DomainException("No hay más registros");
        }

        return new PageImpl<>(lista.subList(inicio, fin), pageRequest, lista.size());
    }
}