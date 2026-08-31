# Historia de Usuario 5 - Actualizar estado de los recursos (Prioridad: P1)

[Los monitores y administradores de recursos necesitan actualizar el estado de los activos universitarios para reflejar su situación real en todo momento. De esta manera, se garantiza que la información de disponibilidad sea precisa para el control de uso.]

**¿Por qué esta prioridad?**: [Es importante para el nucleo operativo del sistema la capacidad de actualizar el estado de los recursos. es Indispensable para mantener la base de datos al día y mantener las consultas confiables.]

**Test Independiente**: [puede probarse de manera independiente seleccionando un recurso ya registrado, cambiando su estado en función de las necesidades de la base y verificando que el cambio se refleje automaticamente en la base de datos.]

**Escenarios de Aceptación**

1. **Escenario**: [Cambiar estado de un recurso]

    - **Dado que** [soy un usuario autorizado para gestionar recursos]
    - **Cuando** [selecciono un recurso y cambio su estado a una de las opciones disponibles en el sistema]
    - **Entonces** [el sistema debe actualizar el estado del recurso y reflejar el cambio en la consulta de disponibilidad]

2. **Escenario**: [Restringir disponibilidad según estado]
    - **Dado que** [un recurso tiene un estado que no permite su uso o préstamo]
    - **Cuando** [un usuario consulta la disponibilidad del recurso]
    - **Entonces** [el sistema debe mostrar el recurso como no disponible para reservas o préstamos]

3. **Escenario**: [Permitir disponibilidad según estado]

    - **Dado que** [un recurso tiene un estado que permite su uso o préstamo]
    - **Cuando** [un usuario consulta la disponibilidad del recurso]
    - **Entonces** [el sistema debe mostrar el recurso como disponible para reservas o préstamos]