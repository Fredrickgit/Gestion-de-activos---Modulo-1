# HU #0: [CONSULTAR DISPONIBILIDAD]:

Como monitor y/o modulo 2 deseo consultar la disponibilidad y el horario asignado de alguno de los recursos ya almacenados en el sistema, ya sea un activo o espacio, para conocer su disponibilidad actual y para una hora o fecha en específico de forma rápida y eficiente.

**Criterios de aceptación:** 

 - El sistema DEBE permitir consultar el estado de disponibilidad actual (Disponible/Reservado/Bloqueo Academico/En uso/En mantenimiento) de cualquier recurso específico (activo o espacio) almacenado en el sistema.

 - El sistema DEBE permitir consultar el horario asignado (horarios de ocupación) de un recurso específico, incluyendo hora de inicio y hora de finalización.
 
 - El sistema DEBE soportar el filtrado de recursos por categoría (ej: "laptop", "microscopio", "workstation", "proyector") y devolver el estado de disponibilidad actual para todos los recursos que coincidan con el filtro

 - El sistema DEBE corroborar todas las entradas de usuario (nombres de recursos, filtros de categoría, campos de fecha/hora) para prevenir ataques de inyección y manejar caracteres especiales adecuadamente.
 
 - El sistema DEBE implementar paginación o limitación de resultados cuando las consultas devuelvan grandes conjuntos de datos, previniendo degradación del rendimiento y problemas de tiempo de espera.

 

