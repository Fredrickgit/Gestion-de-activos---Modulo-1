# Comunicación por Pila / Cola — Perspectiva del Módulo 1 (M1)

**Proyecto:** Gestión de Activos — Módulo 1  
**Última actualización:** 18/09/2026  
**Ámbito:** Comunicación asíncrona, transaccional y priorizada entre M1, M2 y M3 mediante colas persistentes.

---

## 1. Propósito

Definir el contrato de las comunicaciones **asíncronas, confiables y ACID** entre el Módulo 1 (M1) y los módulos M2 y M3. Estas comunicaciones se usan para **eventos críticos del negocio** donde no se puede perder información, se requiere prioridad y se debe garantizar el orden.

> **Nota de terminología:** En el pizarrón original se usa la palabra "Pila". Técnicamente, para garantizar ACID y prioridad, se implementa una **cola persistente con prioridad (Priority Queue)** o un **stack persistente (LIFO)** según el caso de uso. Este documento unifica el concepto bajo el término **Pila/Cola**.

---

## 2. Principios de diseño aplicados

| Principio | Aplicación en M1 |
|-----------|------------------|
| **Persistencia** | Los mensajes sobreviven a caídas de M1, M2 o M3. |
| **Garantía ACID** | Cada evento se procesa como transacción atómica: se completa entero o se revierte. |
| **At-least-once** | Los mensajes se entregan al menos una vez; los consumidores manejan idempotencia. |
| **Prioridad** | Los eventos críticos (uso, daños) tienen mayor prioridad que los informativos. |
| **Orden FIFO / LIFO** | Según tipo de evento: FIFO para reportes acumulativos, LIFO para notificaciones urgentes. |
| **ACK + Reintentos** | El consumidor confirma recepción; si no, el mensaje se reintenta con backoff. |
| **Dead Letter Queue (DLQ)** | Mensajes que fallan tras N reintentos se mueven a DLQ para revisión manual. |

---

## 3. Rol del Módulo 1

Desde la perspectiva de M1, este módulo es el **consumidor ACID principal** de los eventos críticos que M2 y M3 generan durante la operación del sistema. M1 traduce esos eventos en **transiciones de estado** sobre los recursos que custodia, garantizando atomicidad, auditoría y consistencia. Ocasionalmente, M1 también **produce** eventos cuando la Dirección Universitaria cambia un estado manualmente, para que M2 y M3 mantengan su información sincronizada.

| Rol | Descripción |
|-----|-------------|
| **Consumidor primario (aplicador de transiciones)** | M1 escucha los eventos de notificación de uso (emitido por M2 al iniciar una reserva o préstamo), `reserva.finalizada` y `reporte.daños` (emitidos por M3 al cierre de uso o detección de daños). Cada evento desencadena una **transacción ACID** que actualiza el estado del recurso y registra la auditoría correspondiente. |
| **Productor secundario (difusión de cambios manuales)** | Cuando la Dirección Universitaria actualiza un estado directamente en M1 (ej. `Disponible → En Mantenimiento`), M1 emite un evento `estado.actualizado` hacia M2 y M3 para que sus vistas y reglas de negocio se mantengan consistentes. |
| **Garante ACID y de idempotencia** | M1 asegura que cada evento se aplique **una sola vez** (usando `version_recurso` y `evento_id`), con rollback completo ante fallos, y que el resultado quede auditado. Ningún evento puede dejar el recurso en estado inconsistente. |
| **NO es responsable de** | M1 **no** decide si una reserva procede, **no** calcula sanciones, **no** prioriza reservas académicas. Solo **aplica** el resultado de las decisiones tomadas por M2 (reglas) y M3 (cumplimiento). |


