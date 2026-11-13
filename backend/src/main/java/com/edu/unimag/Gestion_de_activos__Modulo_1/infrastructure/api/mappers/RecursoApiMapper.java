package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.DisponibilidadView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.EstadoActualizadoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.PaginaView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.DisponibilidadResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.EstadoActualizadoResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.HorarioOcupacionResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.PageResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce las vistas de aplicación al contrato HTTP de la API.
 */
@Component
public class RecursoApiMapper {

    public PageResponse<RecursoResumenResponse> toPage(PaginaView<RecursoResumenView> pagina) {
        return new PageResponse<>(
                pagina.content().stream().map(this::toResumen).toList(),
                pagina.page(),
                pagina.size(),
                pagina.totalElements());
    }

    public RecursoDetalleResponse toDetalle(RecursoDetalleView view) {
        return new RecursoDetalleResponse(
                view.id(),
                view.codigo(),
                view.nombre(),
                view.tipo(),
                view.estadoFisico(),
                view.estado(),
                view.ubicacion(),
                view.facultad(),
                view.plazoDevolucion(),
                view.aforoMaximo(),
                view.equipamiento());
    }

    public DisponibilidadResponse toDisponibilidad(DisponibilidadView view) {
        HorarioOcupacionResponse horario = view.horarioAsignado() == null
                ? null
                : new HorarioOcupacionResponse(
                        view.horarioAsignado().inicio(),
                        view.horarioAsignado().fin());

        return new DisponibilidadResponse(
                view.id(),
                view.estado(),
                view.disponible(),
                horario,
                view.motivoNoDisponibilidad());
    }

    public EstadoActualizadoResponse toEstadoActualizado(EstadoActualizadoView view) {
        return new EstadoActualizadoResponse(
                view.id().toString(),
                view.tipo(),
                view.estadoAnterior(),
                view.estadoNuevo(),
                view.usuarioResponsable(),
                view.timestamp());
    }

    private RecursoResumenResponse toResumen(RecursoResumenView view) {
        return new RecursoResumenResponse(
                view.id(),
                view.codigo(),
                view.nombre(),
                view.tipo(),
                view.estado(),
                view.estadoFisico(),
                view.facultad(),
                view.ubicacion(),
                view.aforoMaximo(),
                view.plazoDevolucion());
    }
}
