package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.CodeRequiredException;

import java.util.Objects;

public final class Code {

    private final String value;

    private Code(String value) {
        this.value = value;
    }

    public static Code of(String value, String requiredMessage) {
        if (value == null || value.isBlank()) {
            throw new CodeRequiredException(requiredMessage);
        }
        return new Code(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Code code)) {
            return false;
        }
        return value.equals(code.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
