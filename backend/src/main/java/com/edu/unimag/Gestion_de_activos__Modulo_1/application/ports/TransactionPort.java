package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import java.util.function.Supplier;

/**
 * Abstracción mínima del límite transaccional.
 *
 * <p>Permite que los casos de uso permanezcan independientes de Spring y que
 * la infraestructura sea quien decida cómo se implementa la transacción.</p>
 */
public interface TransactionPort {

    <T> T execute(Supplier<T> action);

    void afterCommit(Runnable action);
}
