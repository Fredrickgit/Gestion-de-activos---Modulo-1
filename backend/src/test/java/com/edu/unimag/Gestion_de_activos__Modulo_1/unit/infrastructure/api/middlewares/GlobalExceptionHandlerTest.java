package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.api.middlewares;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.ConflictoValidacionExternaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.EntidadRelacionadaInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoDuplicadoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoNoEncontradoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.SolicitudInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.EstadoSinCambioException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.TransicionEstadoInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ErrorResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/recursos");
    }

    @Test
    @DisplayName("Mapea un error de dominio a 422")
    void manejarDomain() {
        ResponseEntity<ErrorResponse> response = handler.handleDomain(
                new DomainException("La transición solicitada no es válida"), request);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("La transición solicitada no es válida", response.getBody().message());
        assertEquals("/api/v1/recursos", response.getBody().path());
    }

    @Test
    @DisplayName("Mapea un recurso no encontrado a 404")
    void manejarNoEncontrado() {
        ResponseEntity<ErrorResponse> response = handler.handleNoEncontrado(
                new RecursoNoEncontradoException("No se encontró el recurso."), request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("No se encontró el recurso.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea un recurso duplicado a 409")
    void manejarDuplicado() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicado(
                new RecursoDuplicadoException("Ya existe un recurso con el código."), request);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("Ya existe un recurso con el código.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea un estado sin cambio a 409")
    void manejarEstadoSinCambio() {
        ResponseEntity<ErrorResponse> response = handler.handleEstadoSinCambio(
                new EstadoSinCambioException("El recurso ya se encuentra en ese estado."), request);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("El recurso ya se encuentra en ese estado.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea un conflicto de validación externa a 409")
    void manejarConflictoExterno() {
        ResponseEntity<ErrorResponse> response = handler.handleConflictoExterno(
                new ConflictoValidacionExternaException("El recurso tiene una reserva activa."), request);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("El recurso tiene una reserva activa.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea una transición inválida a 400")
    void manejarTransicionInvalida() {
        ResponseEntity<ErrorResponse> response = handler.handleTransicionInvalida(
                new TransicionEstadoInvalidaException("La transición no está permitida."), request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("La transición no está permitida.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea una solicitud inválida a 400")
    void manejarSolicitudInvalida() {
        ResponseEntity<ErrorResponse> response = handler.handleSolicitudInvalida(
                new SolicitudInvalidaException("Debe indicar ubicacionId o edificio."), request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Debe indicar ubicacionId o edificio.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea una entidad relacionada inválida a 422")
    void manejarEntidadInvalida() {
        ResponseEntity<ErrorResponse> response = handler.handleEntidadInvalida(
                new EntidadRelacionadaInvalidaException("La facultad indicada no existe."), request);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("La facultad indicada no existe.", response.getBody().message());
    }

    @Test
    @DisplayName("Mapea una excepción no controlada a 500")
    void manejarGeneral() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(
                new RuntimeException("Error interno"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Ocurrió un error inesperado al procesar la solicitud. Contacte al administrador del sistema.",
                response.getBody().message());
    }
}
