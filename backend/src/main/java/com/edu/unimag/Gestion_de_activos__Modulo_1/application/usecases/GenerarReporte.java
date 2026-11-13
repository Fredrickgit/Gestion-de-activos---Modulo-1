package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

        Comparator<Activo> comparador = switch (campoOrden) {
            case "codigo" -> Comparator.comparing(Activo::getCodigo, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(Activo::getNombre, String.CASE_INSENSITIVE_ORDER);
        };
        comparador = comparador.thenComparing(Activo::getId, Comparator.nullsLast(Comparator.naturalOrder()));

        List<Activo> activosOrdenados = activoRepository.findAll()
                .stream()
                .sorted(comparador)
                .toList();

        PageRequest pageRequest = PageRequest.of(numeroPagina, 20,
                Sort.by(Sort.Direction.ASC, campoOrden).and(Sort.by(Sort.Direction.ASC, "id")));

        int inicio = (int) pageRequest.getOffset();
        int fin = Math.min((inicio + pageRequest.getPageSize()), activosOrdenados.size());

        if (inicio >= activosOrdenados.size() && numeroPagina > 0) {
            throw new DomainException("No hay más registros");
        }

        List<Activo> contenido = activosOrdenados.isEmpty() ? List.of() : activosOrdenados.subList(inicio, fin);
        return new PageImpl<>(contenido, pageRequest, activosOrdenados.size());
    }

    public Page<Activo> previsualizarReporteActivosPorEstado(UUID disponibilidadId, int numeroPagina) {
        List<Activo> activos = activoRepository.findByDisponibilidadId(disponibilidadId);

        if (activos.isEmpty()) {
            throw new DomainException("No hay recursos que coincidan con los filtros");
        }

        return paginarLista(activos, numeroPagina);
    }

    public Page<Espacio> previsualizarReporteEspaciosPorEstado(UUID disponibilidadId, int numeroPagina) {
        List<Espacio> espacios = espacioRepository.findByDisponibilidadId(disponibilidadId);

        if (espacios.isEmpty()) {
            throw new DomainException("No hay recursos que coincidan con los filtros");
        }

        return paginarLista(espacios, numeroPagina);
    }

    private <T> Page<T> paginarLista(List<T> lista, int numeroPagina) {
        PageRequest pageRequest = PageRequest.of(numeroPagina, 20, Sort.by(Sort.Direction.ASC, "id"));
        int inicio = (int) pageRequest.getOffset();
        int fin = Math.min((inicio + pageRequest.getPageSize()), lista.size());

        if (inicio >= lista.size()) {
            throw new DomainException("No hay más registros");
        }

        return new PageImpl<>(lista.subList(inicio, fin), pageRequest, lista.size());
    }
}