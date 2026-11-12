package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions;

public class TransicionEstadoInvalidaException extends RuntimeException {

    public TransicionEstadoInvalidaException(String message) {
        super(message);
    }
}
