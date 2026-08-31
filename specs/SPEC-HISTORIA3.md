# Historia de Usuario 3 - Actualizar el estado del recurso (Prioridad: P1)

[Dirección Universitaria, Monitor de recursos, Módulo 2 o Módulo 3 necesitan poder actualizar el estado de un recurso a lo largo de su ciclo de vida, Para mantener un registro preciso de su disponibilidad y condición, facilitando su categorización y evitando conflictos de horarios o entregas tardías.]

**¿Por qué esta prioridad?:** [Esta función es vital porque el estado de un recurso es el indicador principal de su disponibilidad y usabilidad. Sin esta funcionalidad, el sistema no podría prevenir la doble reserva de recursos ni notificar sobre su mal estado, lo que impacta directamente la operación diaria.]

**Test indepentientes:** [Esta historia puede ser probada de forma independiente al realizar la actualización del estado de un recurso a través de la interfaz y verificando que el cambio se refleje correctamente en la base de datos y que se haya generado un registro de auditoría con el usuario, la hora y el estado anterior.]

**Escenarios de aceptación**

1. **Escenario:** [Actualización exitosa del estado de un recurso]
    - **Dado que:** [soy un usuario autorizado (Dirección Universitaria, Monitor de recursos, Módulo 2 o Módulo 3) y estoy autenticado en el sistema, y que existe un  recurso con un estado actual (ej. "Disponible").]
    - **Cuando:** [selecciono el recurso, elijo un nuevo estado (ej. "En mantenimiento") y confirmo la actualización.]
    - **Entonces:** [el sistema cambia el estado del recurso al nuevo estado seleccionado y muestra un mensaje de confirmación.]

2. **Escenario:** [Registro de auditoría de la actualización]
    - **Dado que:** [soy un usuario autorizado y estoy autenticado, y que un recurso tiene un estado previo (ej. "Disponible").]
    - **Cuando:** [actualizo el estado del recurso a un nuevo estado (ej. "Prestado").]
    - **Entonces:** [el sistema registra en el historial del recurso el usuario que realizó el cambio, la fecha y hora exacta de la modificación, y el estado anterior del recurso.]

3. **Escenario:** [Validación de permisos para actualizar el estado]
    - **Dado que:** [soy un usuario no autorizado (ej. un usuario de Consulta o sin roles asignados) y estoy autenticado en el sistema.]
    - **Cuando:** [intento acceder a la opción de actualizar el estado de un recurso.]
    - **Entonces:** [el sistema deniega la acción y muestra un mensaje de error indicando que no tengo permisos suficientes para realizar esta operación.]

4. **Escenario:** [Intento de actualización a un estado inválido]
    - **Dado que:** [soy un usuario autorizado y estoy en la pantalla de actualización de estado de un recurso.]
    - **Cuando:** [intento seleccionar o asignar un estado que no está dentro del flujo de vida válido (ej. intentar pasar un recurso de "En mantenimiento" directamente a "Disponible" cuando se requiere una revisión intermedia).]
    - **Entonces:** [el sistema muestra un mensaje de error indicando que la transición de estado no es permitida y no realiza el cambio.]

