Historia de usuario 1 - Creacion de recurso (PRIORIDAD: P1) 

[Direccion universitaria desea agregar un nuevo recurso al sistema, ya sea un activo o espacio, dependiendo del tipo de recurso direccion universitaria quiere agregar los atributos respectivos del recurso (nombre; ID, aforo máximo, equipamiento fijo y facultad; o ID 'Placa de inventario', tipo, estado físico y ubicación)]

Por qué esta prioridad?
[El modelo de creacion es la base de los recursos los cuales son el eje central de la aplicacion, esto es importante para la creacion, el crecimiento y el mantenimiento del sistema a largo plazo. Esta opcion esta definida por prerrequisitos dependiendo del tipo de recurso lo que permite a los usuarios explorar las variedades de opciones de creacion de estos.]

Test independiente
[La historia de usaurio se puede testear mediente la creacion de un activo y su correcta aparicion en la base de datos con los atributos asignados]

Acceptance Scenaries
#1 Creacion activo exitosa
[Given - Dado que soy un usuario de Direccion Universitaria 
 When - Cuando ingreso nomnbre, placa de inventario, tipo computador, ubicacion laboratorio simulacion, estado fisico nuevo de un recurso nuevo y lo envio
 Then - El sistema valida la informacion y se crea el activo en la base de datos ]

#2 Informacion faltante en activo
[Given - Dado que soy un usuario de Direccion Universitaria
 When - Cuando ingreso el formulario sin llenar el atributo 'tipo' de un activo y lo envio
 Then - El sistema indica la informacion faltante y evita la creacion del activo ]
 
#3 Creacion espacio exitosa 
[Given - Dado que soy un usuario de Direccion Universitaria
 When - Cuando ingreso el nomnbre, aforo maximo 30, equipamiento fijo 25 computadoras 25 sillas, ubicacion hangares, facultad ingenieria, y lo envio
 Then - El sistema valida la informacion y se crea el espacio en la base de datos ]

#4 Informacion faltante en un espacio
[Given - Dado que soy un usuario de Direccion Universitaria 
 When - Cuando ingreso el formulario sin llenar el atributo 'aforo maximo' de un espacio y lo envio
 Then - El sistema indica la informacion faltante y evita la creacion del espacio ]
