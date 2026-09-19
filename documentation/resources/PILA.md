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

Desde la perspectiva de M1, este módulo es principalmente **consumidor** de eventos críticos emitidos por M2 y M3, y en algunos casos **productor** de eventos hacia ellos.

| Rol | Descripción |
|-----|-------------|
| **Consumidor primario** | M1 escucha eventos de uso, daños y reservas finalizadas para actualizar el estado de los recursos. |
| **Productor ocasional** | M1 emite eventos de confirmación o rechazo tras procesar una transacción. |
| **Garante ACID** | M1 asegura que cada evento se aplique atómicamente a la base de datos, con auditoría. |

---


