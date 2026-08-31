# Historia de Usuario 2 Consultar Inventarios (Prioridad: P1)

[La Dirección Universitaria y los monitores encargados de revisar los recursos necesitan un medio para consultar el inventario de recursos. De esta manera, podrán visualizar los recursos registrados y sus respectivos atributos. Asimismo, si necesitan revisar un recurso en concreto, podrán encontrarlo mediante la búsqueda utilizando estos mismos atributos.]

**¿Por qué esta prioridad?**: [Es importante para la utilidad del sistema permitir el registro y la consulta de los recursos almacenados en la base de datos.]

**Test Independiente**: [Puede probarse de manera independiente verificando que al ingresar a la pantalla de inventario se muestren todos los recursos registrados con sus atributos, y que al utilizar los filtros de búsqueda aparezcan únicamente los recursos que coinciden con la búsqueda.]


**Escenarios de Aceptación**


1. **Escenario**: [Visualización de inventario]

   - **Dado que** [soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **Cuando** [Accedo al inventario para revisar los recursos]
   - **Entonces** [El sistema debe mostrar los recursos previamente registrados]

2. **Escenario**: [Buscar un recurso]

   - **Dado que** [soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **Cuando** [Realizo una consulta utilizando un criterio válido, como el nombre del recurso, aforo, ID o facultad]
   - **Entonces** [El sistema debe mostrar los recursos cuyos atributos coincidan con los criterios de búsqueda]

3. **Escenario**: [Inventario sin resultados]

   - **Dado** [soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **Cuando** [Realizo una consulta y los criterios de búsqueda no coinciden con ningún recurso]
   - **Entonces** [El sistema debe informar que no se encontraron resultados para los criterios de búsqueda]

---
