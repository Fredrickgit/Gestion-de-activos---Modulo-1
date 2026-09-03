# Historia de Usuario 3 - Actualizar el estado del recurso (Prioridad: P1)

[La Dirección Universitaria, Monitor de recursos, Módulo 2 o Módulo 3 necesitan poder actualizar el estado de un recurso a lo largo de su ciclo de vida, Para mantener un registro preciso de su disponibilidad y condición, facilitando su categorización y evitando conflictos de horarios o entregas tardías.]

**¿Por qué esta prioridad?:** [Esta función es vital porque el estado de un recurso es el indicador principal de su disponibilidad y usabilidad. Sin esta funcionalidad, el sistema no podría prevenir la doble reserva de recursos ni notificar sobre su mal estado, lo que impacta directamente la operación diaria.]

**Test indepentientes:** [Esta historia puede ser probada de forma independiente al realizar la actualización del estado de un recurso a través de la interfaz y verificando que el cambio se refleje correctamente en la base de datos y que se haya generado un registro de auditoría con el usuario, la hora y el estado anterior.]

**Escenarios de aceptación**

1. **Escenario:** [Actualización exitosa del estado de un recurso]
    - **Dado:** [Soy un usuario autorizado (Dirección Universitaria, Monitor de recursos, Módulo 2 o Módulo 3) y estoy autenticado en el sistema, y que existe un recurso con un estado actual (ej. "Disponible").]
    - **Cuando:** [Selecciono el recurso, elijo un nuevo estado (ej. "En mantenimiento") y confirmo la actualización.]
    - **Entonces:** [El sistema cambia el estado del recurso al nuevo estado seleccionado y muestra un mensaje de confirmación.]

2. **Escenario:** [Registro de auditoría de la actualización]
    - **Dado:** [Soy un usuario autorizado y estoy autenticado, y que un recurso tiene un estado previo (ej. "Disponible").]
    - **Cuando:** [Actualizo el estado del recurso a un nuevo estado (ej. "Prestado").]
    - **Entonces:**[El sistema registra en el historial del recurso el usuario que realizó el cambio, la fecha y hora exacta de la modificación, y el estado anterior del recurso.]

3. **Escenario:** [Validación de permisos para actualizar el estado]
    - **Dado:** [Soy un usuario no autorizado (ej. un usuario de Consulta o sin roles asignados) y estoy autenticado en el sistema.]
    - **Cuando:** [Intento acceder a la opción de actualizar el estado de un recurso.]
    - **Entonces:** [El sistema deniega la acción y muestra un mensaje de error indicando que no tengo permisos suficientes para realizar esta operación.]

4. **Escenario:** [Intento de actualización a un estado inválido]
    - **Dado:** [Soy un usuario autorizado y estoy en la pantalla de actualización de estado de un recurso.]
    - **Cuando:** [Intento seleccionar o asignar un estado que no está dentro del flujo de vida válido (ej. intentar pasar un recurso de "En mantenimiento" directamente a "Disponible" cuando se requiere una revisión intermedia).]
    - **Entonces:** [El sistema muestra un mensaje de error indicando que la transición de estado no es permitida y no realiza el cambio.]


### Edge Cases

- [El usuario intenta acceder al módulo o funcionalidad de actualización sin tener permisos de Dirección Universitaria]
El sistema debe validar los permisos del usuario y devolver un mensaje de acceso denegado.

- [si un usuario intenta cambiar el estado de un recurso a su mismo estado actual (transición redundante)]
El sistema verificará si el nuevo estado es estrictamente igual al estado actual. Si es asi, este mismo respondera con un
"El recurso ya se encuentra en el estado seleccionado."

- [Dos usuarios autorizados intentan actualizar el estado del mismo recurso simultáneamente]
El sistema bloqueará el cambio al detectar información obsoleta, notificará al segundo usuario y refrescará automáticamente la pantalla con el estado actualizado.

- [Al actualizar los estados de los recursos que actualmente están vinculados a reservas o préstamos activos] 
El sistema restringirá transiciones arbitrarias que entren en conflicto con la operación activa, Si un recurso se encuentra en un estado como "Reservado" o "Bloqueo Academico"

- [La conexión de red se interrumpe a mitad de la transacción durante la modificación del estado]
El sistema realiza una reboninación completa. No se guardará ni el cambio de estado ni el log parcial, Si la conexión falla antes de que la transacción se complete

## Requirements

### Functional Requirements

- **FR-001:** El sistema DEBE permitir a los roles autorizados (Dirección Universitaria, Monitor de recursos, Módulo 2, Módulo 3) actualizar el estado de un recurso.  

- **FR-002:** El sistema DEBE restringir la actualización de estados a usuarios sin permisos explícitos (ej. perfil Consulta/Lectura).  

- **FR-003:** El sistema DEBE hacer cumplir las reglas de transición del ciclo de vida del recurso según la máquina de estados definida.  

- **FR-004:** El sistema DEBE generar un registro de auditoría por cada cambio de estado, almacenando ID de usuario, marca de tiempo, estado anterior y nuevo estado.  

- **FR-005:** El sistema DEBE mostrar mensajes de retroalimentación claros al usuario tras el éxito o fallo de la actualización

### Key Entities 

- **[Entity 1]**: [Activo] [ID / Placa de inventario, nombre, tipo {enlazado a tipo-Activo}, estado físico, ubicación {enlazado a espacio}, estado de disponibilidad inicial (ej. Disponible)]

- **[Entity 2]**: [Espacio] [ID, nombre, aforo máximo, equipamiento {enlazado a activo o catálogo de equipamiento}, ubicación {enlazado a su propia entidad}, facultad {enlazado a su propia entidad}, estado de disponibilidad inicial (ej. Disponible)]

## Success Criteria

### Measurable Outcomes

- **FR-001:** El 100% de las modificaciones de estado generan un registro de auditoría completo con marca de tiempo precisa, usuario responsable y estado previo. 

- **FR-002:** El 0% de las transiciones de estado no permitidas son procesadas por el sistema.

- **FR-003:** Los intentos de cambio de estado por usuarios no autorizados son bloqueados en el 100% de los casos. 

- **FR-004:** Los cambios de estado se reflejan en la base de datos y en la interfaz de usuario en menos de 1 segundo bajo carga nominal.