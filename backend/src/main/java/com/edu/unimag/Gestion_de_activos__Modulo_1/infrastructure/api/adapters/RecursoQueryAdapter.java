package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.RecursoQueryPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.repository.RecursoReadRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador del modelo de lectura SQL hacia las vistas de aplicación.
 */
@Component
public class RecursoQueryAdapter implements RecursoQueryPort {

    private final RecursoReadRepository recursoReadRepository;

    public RecursoQueryAdapter(RecursoReadRepository recursoReadRepository) {
        this.recursoReadRepository = recursoReadRepository;
    }

    @Override
    public List<RecursoResumenView> listar(String tipo,
                                           String estado,
                                           UUID facultadId,
                                           String facultadNombre,
                                           int page,
                                           int size) {
        return recursoReadRepository.listar(tipo, estado, facultadId, facultadNombre, page, size)
                .stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public long contar(String tipo,
                       String estado,
                       UUID facultadId,
                       String facultadNombre) {
        return recursoReadRepository.contar(tipo, estado, facultadId, facultadNombre);
    }

    @Override
    public Optional<RecursoDetalleView> findDetalle(UUID id) {
        return recursoReadRepository.findDetalle(id).map(this::toView);
    }

    private RecursoResumenView toView(RecursoResumenResponse response) {
        return new RecursoResumenView(
                response.id(),
                response.codigo(),
                response.nombre(),
                response.tipo(),
                response.estado(),
                response.estadoFisico(),
                response.facultad(),
                response.ubicacion(),
                response.aforoMaximo(),
                response.plazoDevolucion());
    }

    private RecursoDetalleView toView(RecursoDetalleResponse response) {
        return new RecursoDetalleView(
                response.id(),
                response.codigo(),
                response.nombre(),
                response.tipo(),
                response.estadoFisico(),
                response.estado(),
                response.ubicacion(),
                response.facultad(),
                response.plazoDevolucion(),
                response.aforoMaximo(),
                response.equipamiento());
    }
}
