package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidLoanPeriodException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.LoanPeriod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanPeriodTest {

    @Test
    @DisplayName("Crea un plazo de devolucion positivo")
    void of_creaPlazoPositivo() {
        LoanPeriod loanPeriod = LoanPeriod.of(5);

        assertThat(loanPeriod.value()).isEqualTo(5);
    }

    @Test
    @DisplayName("Permite un plazo de devolucion cero")
    void of_permitePlazoCero() {
        LoanPeriod loanPeriod = LoanPeriod.of(0);

        assertThat(loanPeriod.value()).isZero();
    }

    @Test
    @DisplayName("Rechaza el plazo de devolucion negativo")
    void of_rechazaPlazoNegativo() {
        assertThatThrownBy(() -> LoanPeriod.of(-1))
                .isInstanceOf(InvalidLoanPeriodException.class)
                .hasMessage("El plazo de devolucion no puede ser negativo");
    }

    @Test
    @DisplayName("Dos plazos con el mismo valor son iguales")
    void equals_comparaPorValor() {
        LoanPeriod loanPeriod = LoanPeriod.of(5);
        LoanPeriod sameLoanPeriod = LoanPeriod.of(5);

        assertThat(loanPeriod).isEqualTo(sameLoanPeriod);
        assertThat(loanPeriod).hasSameHashCodeAs(sameLoanPeriod);
    }

    @Test
    @DisplayName("Dos plazos con distinto valor no son iguales")
    void equals_rechazaValoresDistintos() {
        assertThat(LoanPeriod.of(5)).isNotEqualTo(LoanPeriod.of(10));
    }
}
