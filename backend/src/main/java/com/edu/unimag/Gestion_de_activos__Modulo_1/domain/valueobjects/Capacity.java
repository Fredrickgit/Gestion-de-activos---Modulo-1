package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCapacityException;

import java.util.Objects;

public final class Capacity {

    private final int value;

    private Capacity(int value) {
        this.value = value;
    }

    public static Capacity of(int value) {
        if (value <= 0) {
            throw new InvalidCapacityException("El aforo del espacio debe ser mayor que cero");
        }
        return new Capacity(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Capacity capacity)) {
            return false;
        }
        return value == capacity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
