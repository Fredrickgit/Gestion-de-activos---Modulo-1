Historia de usuario 0 - Consultar disponibilidad del recurso (PRIORIDAD: P3) 

[El monitor de Activos/Espacios y/o el modulo 2 desea consultar la disponibilidad y el horario asignado de alguno de los recursos ya almacenados en el sistema, ya sea un activo o espacio, para conocer su disponibilidad actual y para una hora o fecha en específico de forma rápida y eficiente.]

Por qué esta prioridad?
[La consulta de horarios libres y reservados es fundamental para el funcionamiento del sistema, de parte del actor que monitorea y del modulo 2 de la aplicación, sin esta el resto de funcionalidades objetivo del sistema no tienen forma de funcionar. ]

Test independiente
[La historia de usuario se puede testear con una peticion de consulta que devuelva correctamente la disponibilidad actual de un recurso en específico y su horario asignado en caso de existir. ]

Escenarios de aceptación
#1 Verificación exitosa de disponibilidad de un activo
[Given - Dado que soy el monitor de recursos o el modulo 2 
 When - Consulto por la información del 'microscopio #5'
 Then - Puedo ver su estado actual de disponibilidad (En uso) ]

#2 Verificación exitosa de disponibilidad de un espacio
[Given - Dado que soy el monitor de recursos o el modulo 2 
 When - Consulto por la información del laboratorio 'Telecomunicaciones'
 Then - Puedo ver su estado actual de disponibilidad (Libre) y su horario de ocupación. ]
 
#3 Verificación exitosa de una categoria de activo
[Given - Dado que soy el monitor de recursos o el modulo 2 
 When - Cuando ingreso un filtro de categoria para consultar los activos 'laptop'
 Then - El sistema valida la informacion y devuelve la disponibilidad actual de los activos de esa categoria ]

