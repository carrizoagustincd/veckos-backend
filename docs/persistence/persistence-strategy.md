# Estrategia De Persistencia Backend

## Objetivo
Definir la estrategia de persistencia del nuevo backend para el dominio multi-tenant.

## Stack Objetivo
- PostgreSQL
- Flyway
- jOOQ
- JDBC en casos puntuales

## Principios
- schema-first
- queries explicitas y trazables
- aislamiento tenant-aware obligatorio
- performance como criterio de diseño, no como optimizacion tardia
- separacion clara entre dominio y persistencia

## Tecnologia Principal

### jOOQ
Se adopta `jOOQ` como tecnologia principal de acceso a datos para el nuevo dominio.

Razones:
- expresividad SQL tipada
- control explicito de joins, filtros y agregaciones
- mejor soporte para reportes y lecturas complejas
- alta visibilidad del costo de las queries
- mejor compatibilidad con multi-tenancy por `tenant_id`

## Tecnologia Complementaria

### JDBC
`JDBC` se admite como complemento cuando exista un beneficio claro.

Casos donde puede ser buena opcion:
- operaciones muy simples y directas
- lecturas o escrituras extremadamente acotadas
- integracion de bajo nivel donde no haga falta el DSL completo de jOOQ
- puntos infrastructurales donde se priorice minima sobrecarga

Regla:
- JDBC no debe usarse de forma arbitraria ni por preferencia personal sin justificacion tecnica
- si jOOQ resuelve mejor el caso, jOOQ sigue siendo la opcion por defecto

## Tecnologia Descartada Como Base Principal

### JPA / Hibernate
No se adopta como base principal del nuevo dominio.

Motivos:
- demasiada logica implicita para este dominio
- complejidad accidental en relaciones y carga lazy
- peor alineacion con reporting y queries complejas
- mayor riesgo de acoplar modelo de dominio a persistencia

## Estrategia De Multi-Tenancy En Persistencia
- todas las tablas tenant-scoped deben tener `tenant_id`
- toda query tenant-scoped debe filtrar explicitamente por `tenant_id`
- no se permite acceso a datos de negocio sin conciencia de tenant

## Relacion Con Migraciones

### Flyway
Se adopta `Flyway` como mecanismo de evolucion de schema.

Reglas:
- cada cambio estructural viaja en una migracion versionada
- no depender de `ddl-auto=update` para evolucion de schema real
- las migraciones deben reflejar el modelo canónico del dominio

## Organizacion Recomendada En Infrastructure

### Repositories
Cada modulo del nuevo backend deberia tener repositories en `infrastructure` con esta idea:
- jOOQ para la mayoria de lecturas/escrituras de dominio
- JDBC solo para casos puntuales y justificados

### Mapeo
- no exponer registros jOOQ ni filas JDBC fuera de infrastructure
- mapear a modelos de dominio o DTOs internos segun corresponda

## Tipos De Acceso Recomendados

### Escrituras de dominio
Preferencia:
- jOOQ

### Lecturas complejas y reportes
Preferencia:
- jOOQ

### Operaciones simples o infrastructurales
Preferencia:
- JDBC si simplifica sin degradar consistencia

## Performance
La performance de queries se considera una preocupacion de primera clase.

Consecuencias practicas:
- evitar queries implicitas o dificiles de auditar
- priorizar lecturas explicitas
- diseñar indices y constraints junto con el schema
- separar lecturas de reporting de reglas de escritura cuando haga falta

## Evolucion Esperada
En etapas posteriores deberia agregarse documentacion complementaria para:
- convenciones de naming de tablas y columnas nuevas
- organizacion de migraciones Flyway
- estrategia de generacion de codigo jOOQ
- lineamientos de repositories por modulo
