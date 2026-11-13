package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidLoanPeriodException;

import java.util.Objects;

public final class LoanPeriod {

    private final int value;

    private LoanPeriod(int value) {
        this.value = value;
    }

    public static LoanPeriod of(int value) {
        if (value < 0) {
            throw new InvalidLoanPeriodException("El plazo de devolucion no puede ser negativo");
        }
        return new LoanPeriod(value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoanPeriod loanPeriod)) {
            return false;
        }
        return value == loanPeriod.value;
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
