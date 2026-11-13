package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;

import java.util.Objects;

public final class Name {

    private final String value;

    private Name(String value) {
        this.value = value;
    }

    public static Name of(String value, String requiredMessage) {
        if (value == null || value.isBlank()) {
            throw new NameRequiredException(requiredMessage);
        }
        return new Name(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Name name)) {
            return false;
        }
        return value.equals(name.value);
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
