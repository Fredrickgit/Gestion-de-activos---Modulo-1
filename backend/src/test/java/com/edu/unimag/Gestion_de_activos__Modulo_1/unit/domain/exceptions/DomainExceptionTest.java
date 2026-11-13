package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.exceptions;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.AvailabilityRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.CodeRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCapacityException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCategoryException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCoordinatesException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidLoanPeriodException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.PhysicalStateRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.RequiredReferenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Test
    @DisplayName("DomainException extiende RuntimeException")
    void domainException_extiendeRuntimeException() {
        assertThat(new DomainException("error")).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Todas las excepciones especificas extienden DomainException")
    void excepcionesEspecificas_extiendenDomainException() {
        assertThat(new NameRequiredException("error")).isInstanceOf(DomainException.class);
        assertThat(new CodeRequiredException("error")).isInstanceOf(DomainException.class);
        assertThat(new InvalidCategoryException("error")).isInstanceOf(DomainException.class);
        assertThat(new RequiredReferenceException("error")).isInstanceOf(DomainException.class);
        assertThat(new InvalidCapacityException("error")).isInstanceOf(DomainException.class);
        assertThat(new InvalidLoanPeriodException("error")).isInstanceOf(DomainException.class);
        assertThat(new PhysicalStateRequiredException("error")).isInstanceOf(DomainException.class);
        assertThat(new AvailabilityRequiredException("error")).isInstanceOf(DomainException.class);
        assertThat(new InvalidCoordinatesException("error")).isInstanceOf(DomainException.class);
    }
}
