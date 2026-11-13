package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ErrorResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.ConflictoValidacionExternaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.EntidadRelacionadaInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoDuplicadoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.RecursoNoEncontradoException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.exceptions.SolicitudInvalidaException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.EstadoSinCambioException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.TransicionEstadoInvalidaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Manejador global de errores de la API. Normaliza todas las respuestas al
 * contrato definido en specs/REST.md §6.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(RecursoDuplicadoException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(EstadoSinCambioException.class)
    public ResponseEntity<ErrorResponse> handleEstadoSinCambio(EstadoSinCambioException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictoValidacionExternaException.class)
    public ResponseEntity<ErrorResponse> handleConflictoExterno(ConflictoValidacionExternaException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionInvalida(TransicionEstadoInvalidaException ex,
                                                                  HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleSolicitudInvalida(SolicitudInvalidaException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(EntidadRelacionadaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleEntidadInvalida(EntidadRelacionadaInvalidaException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex,
                                                      HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatearFieldError)
                .distinct()
                .collect(Collectors.joining("; "));

        String mensajeGlobal = ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido.")
                .collect(Collectors.joining("; "));

        String detalle = unirMensajes(mensaje, mensajeGlobal);
        if (detalle.isBlank()) {
            detalle = "La solicitud contiene campos inválidos o faltantes.";
        }
        return build(HttpStatus.BAD_REQUEST, detalle, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex,
                                                                HttpServletRequest request) {
        String detalleFromParams = ex.getParameterValidationResults().stream()
                .flatMap(resultado -> resultado.getResolvableErrors().stream())
                .map(error -> error.getDefaultMessage() != null
                        ? error.getDefaultMessage()
                        : "Parámetro inválido.")
                .collect(Collectors.joining("; "));

        String detalleCrossParams = ex.getCrossParameterValidationResults().stream()
                .map(error -> error.getDefaultMessage() != null
                        ? error.getDefaultMessage()
                        : "Parámetro inválido.")
                .collect(Collectors.joining("; "));

        String detalle = unirMensajes(detalleFromParams, detalleCrossParams);

        if (detalle.isBlank()) {
            detalle = "Los parámetros de la solicitud no son válidos.";
        }
        return build(HttpStatus.BAD_REQUEST, detalle, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        String detalle = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (detalle.isBlank()) {
            detalle = "Los parámetros de la solicitud no son válidos.";
        }
        return build(HttpStatus.BAD_REQUEST, detalle, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String mensaje;
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(java.util.UUID.class)) {
            mensaje = "El identificador '" + ex.getValue() + "' no tiene formato UUID válido.";
        } else {
            mensaje = "El parámetro '" + ex.getName() + "' tiene un formato inválido.";
        }
        return build(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBodyInvalido(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es válido o contiene valores no permitidos.", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridad(DataIntegrityViolationException ex,
                                                          HttpServletRequest request) {
        log.warn("Violación de integridad al procesar {}: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad de datos (registro duplicado o referencia inválida).",
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado al procesar la solicitud. Contacte al administrador del sistema.",
                request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    private String formatearFieldError(FieldError error) {
        String mensaje = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido.";
        return error.getField() + ": " + mensaje;
    }

    private String unirMensajes(String primero, String segundo) {
        if (primero == null || primero.isBlank()) {
            return segundo == null ? "" : segundo;
        }
        if (segundo == null || segundo.isBlank()) {
            return primero;
        }
        return primero + "; " + segundo;
    }
}
