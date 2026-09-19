# Comunicación REST — Perspectiva del Módulo 1 (M1)

**Proyecto:** Gestión de Activos — Módulo 1  
**Última actualización:** 18/09/2026  
**Ámbito:** Comunicación síncrona entre M1, M2 y M3 mediante API REST sobre HTTP.

---

## 1. Propósito

Definir el contrato de las comunicaciones **síncronas, ligeras y stateless** entre el Módulo 1 (M1) y los módulos M2 y M3. Estas comunicaciones se usan exclusivamente para **consultas** y **operaciones de lectura/escritura simples** donde no se requiere garantía ACID entre módulos ni persistencia de eventos.

---

## 2. Principios de diseño REST aplicados

| Principio | Aplicación en M1 |
|-----------|------------------|
| **Stateless** | Cada petición contiene toda la información necesaria; M1 no guarda sesión del cliente entre llamadas. |
| **Cliente-Servidor** | M1 actúa como servidor de recursos; M2 y M3 son clientes. |
| **Cacheable** | Las respuestas de consultas (GET) incluyen headers `Cache-Control` cuando aplica. |
| **Interfaz uniforme** | Uso de métodos HTTP estándar (GET, POST, PUT, PATCH, DELETE) y URIs que representan recursos. |
| **Sistema en capas** | M1 puede delegar a otros servicios internos sin que M2/M3 lo sepan. |
| **Versionado** | Toda ruta incluye `/api/v1/` para permitir evolución sin romper contratos. |

---

## 3. Rol del Módulo 1

Desde la perspectiva de M1, este módulo es la autoridad del inventario físico y del estado de los recursos. Su función principal es exponer los datos maestros de activos y espacios a los demás módulos y a los usuarios autorizados. Solo consume REST de forma puntual para validaciones cruzadas antes de permitir transiciones críticas de estado.

| Rol | Descripción |
|-----|-------------|
| **Servidor REST principal (autoridad del inventario)** | M1 expone el catálogo de recursos, la consulta de disponibilidad, la actualización de estados y la generación de reportes de inventario. Es la fuente de verdad de los atributos de cada Activo y Espacio, y del ciclo de vida (`Disponible`, `En Uso`, `En Mantenimiento`). |
| **Cliente REST ocasional (validación cruzada)** | Antes de permitir ciertas transiciones de estado, M1 consulta a M2 si el recurso tiene reservas activas vigentes (para no liberar un recurso reservado) y a M3 si el recurso tiene novedades técnicas pendientes. Estas consultas son síncronas y de solo lectura, con timeout corto. |
| **NO es responsable de** | M1 no valida sanciones de usuarios ni gestiona reservas ni aplica penalizaciones. Esas son responsabilidades de M2 (reglas de reserva) y M3 (sanciones y analítica). M1 solo almacena el estado resultante. |
