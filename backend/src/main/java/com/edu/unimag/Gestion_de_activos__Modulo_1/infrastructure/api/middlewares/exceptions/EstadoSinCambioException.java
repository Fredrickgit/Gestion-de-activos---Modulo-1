package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions;

public class EstadoSinCambioException extends RuntimeException {

    public EstadoSinCambioException(String message) {
        super(message);
    }
}
