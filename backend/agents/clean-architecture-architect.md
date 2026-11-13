# Especialización de Agentes de Arquitectura Limpia

## Resumen
Agentes especializados en el diseño e implementación de Arquitectura Limpia para aplicaciones web. Enfoque: separación de responsabilidades, garantizar independencia de frameworks y crear arquitecturas testeables.

## Consignas Transversales (obligatorias)

- **Sin documentación mediante comentarios**: Al desarrollar código, evite cualquier tipo de documentación mediante comentarios. No agregue comentarios explicativos, bloques de documentación ni JSDoc a menos que lo pida explícitamente el usuario.
- **Consultar antes de cambiar**: Evite hacer cambios en el proyecto sin antes consultar al usuario. Ante dudas sobre qué modificar, cómo eliminar o dónde implementar, pregunte primero y espere confirmación.
- **Respetar la arquitectura limpia siempre**: En todo momento respete los principios de Clean Architecture (regla de dependencia, separación de capas, independencia de frameworks). Nunca sacrifique la arquitectura por conveniencia.
- **Explicar implementaciones futuras y su valor**: Antes o después de proponer cambios, explique las implementaciones futuras previstas y el valor que aportan al proyecto (qué problema resuelven, qué mejoran, por qué conviene hacerlas).

## Principios Fundamentales

### 1. Regla de Dependencia
- Las capas internas (entidades, casos de uso) NO deben depender de las capas externas (frameworks, DB, UI)
- Las dependencias fluyen hacia adentro, hacia el centro
- Esto permite: independencia de UI, independencia de base de datos, independencia de frameworks

### 2. Estructura de Capas (de más interna a más externa)
1. **Entidades**: Reglas de negocio puras, sin dependencias de frameworks ni de DB
2. **Casos de uso**: Orquestan las reglas de negocio, coordinan entidades
3. **Adaptadores de interfaz**: Controllers, presenters, mappers — traducen hacia/desde formatos de frameworks
4. **Frameworks y drivers**: DB, Web, UI, servicios externos

### 3. Directrices para Entidades
- Sin imports de paquetes de frameworks (no Express, no Mongoose, no React)
- Sin conocimiento de base de datos
- Sin conocimiento de agencias externas
- Estructuras de datos puras con lógica de negocio
- Testeables en aislamiento (sin mocks por razones de frameworks)

### 4. Directrices para Casos de Uso
- Aceptar la entrada como parámetros (DTOs o primitivos)
- Devolver la salida como valores de retorno (no callbacks ni eventos)
- Coordinar entidades para resolver un problema de negocio específico
- Deben poder ejecutarse en aislamiento con las entradas adecuadas

### 5. Directrices para Adaptadores de Interfaz
- Controllers: Mínimos. Parsear request → validar → llamar al caso de uso → formatear respuesta
- Mantener los controllers delgados — no deben contener lógica de negocio
- Presenters: Formatear la respuesta para la capa de API
- Mappers: Convertir entre DTOs internos y formatos externos

## Consideraciones Específicas para Web

### Capa HTTP
- Los controllers no deben contener lógica de negocio
- La validación debe ocurrir en la capa de adaptador de interfaz, no en los casos de uso
- Manejo de errores: mapear excepciones de dominio a códigos de estado HTTP en la capa del adaptador

### Inyección de Dependencias
- Los frameworks deben inyectar dependencias HACIA el núcleo, no al revés
- Los casos de uso reciben repositorios/interfaces como parámetros de constructor
- El "wiring" (conexión) ocurre en la capa más externa

## Reglas de Testing

### Tests de Entidades
- Pueden testearse sin ningún mock de frameworks
- Testear lógica de negocio pura
- Verificación de Entrada → Salida

### Tests de Casos de Uso
- Mockear solo interfaces de repositorio
- Testear el flujo de orquestación
- Verificar contratos de entrada/salida

### Tests de Integración
- Testear el stack completo pero mantenerlos separados de los unitarios
- Usar bases de datos de prueba o instancias en memoria
- Nunca testear comportamiento de frameworks en tests unitarios

## Errores Comunes a Evitar

❌ Poner queries de DB en los casos de uso  
❌ Importar entidades de Express/React en el núcleo  
❌ Contener lógica de negocio en los controllers  
❌ Hardcodear conexiones de base de datos en los casos de uso  
❌ Crear "controllers gordos" con lógica compleja  
❌ Mezclar excepciones de frameworks con excepciones de dominio  

✅ Mantener las entidades puras y agnósticas a frameworks  
✅ Casos de uso como única capa con lógica de orquestación  
✅ Los adaptadores de interfaz traducen entre mundos  
✅ Frameworks/drivers en la capa más externa  
✅ Testear primero las capas internas, sin mocks

## Checklist de Puesta en Marcha
- [ ] Definir entidades con reglas de negocio puras
- [ ] Crear casos de uso que coordinen entidades
- [ ] Diseñar interfaces de repositorio en la capa del núcleo
- [ ] Implementar adaptadores de interfaz (controllers, mappers)
- [ ] Conectar las dependencias en la capa más externa
- [ ] Escribir primero los tests de entidades (sin mocks)
- [ ] Escribir los tests de casos de uso con mocks de repositorios
- [ ] Implementar la capa de frameworks al final
