package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.TransactionPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Adaptador transaccional basado en Spring. Mantiene la capa de aplicación
 * libre de anotaciones y clases de infraestructura.
 */
@Component
public class TransactionAdapter implements TransactionPort {

    private final TransactionTemplate transactionTemplate;

    public TransactionAdapter(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }

    @Override
    public void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
