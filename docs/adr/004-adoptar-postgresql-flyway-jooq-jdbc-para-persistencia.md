# ADR 004 - Adoptar PostgreSQL + Flyway + jOOQ + JDBC Para Persistencia

## Estado
Aprobado

## Contexto
El proyecto esta por iniciar la implementacion del nuevo foundation multi-tenant y necesita decidir una estrategia de persistencia antes de construir los nuevos modulos de dominio.

La solucion actual basada en JPA/Hibernate mostro problemas concretos:
- demasiada complejidad implicita
- dificultad para razonar relaciones y carga lazy
- fragilidad en serializacion y boundaries de API
- poca claridad para consultas complejas y reportes

Ademas, el dominio objetivo requiere:
- control explicito del acceso a datos por `tenant_id`
- buen soporte para reporting y agregaciones
- evolucion de schema con migraciones versionadas
- alta trazabilidad sobre queries y performance

## Decision
Adoptar esta estrategia objetivo de persistencia para el nuevo backend:
- PostgreSQL como motor principal
- Flyway para migraciones versionadas
- jOOQ como tecnologia principal de acceso a datos
- JDBC como complemento en casos puntuales donde aporte simplicidad, performance o integracion de bajo nivel

No se adopta JPA/Hibernate como tecnologia principal para el nuevo dominio.

## Razonamiento

### Por que PostgreSQL
- maduro y robusto para SaaS multi-tenant
- excelente soporte SQL y de integridad
- muy buen encaje con reporting y agregaciones

### Por que Flyway
- simple y directo
- excelente integracion con schema-first
- buena convivencia con jOOQ

### Por que jOOQ
- consultas expresivas y tipadas
- mayor control sobre SQL real
- mejor trazabilidad para performance
- mejor encaje para reporting y queries complejas
- facilita imponer aislamiento por `tenant_id` de manera explicita

### Por que sumar JDBC
- util para operaciones simples o de muy bajo nivel
- util para casos donde jOOQ sea innecesario o demasiado pesado
- util para accesos infrastructurales concretos

## Consecuencias

### Positivas
- mayor control sobre persistencia y performance
- menos magia implicita que con JPA/Hibernate
- mejor soporte para consultas complejas y reportes
- mejor alineacion con multi-tenancy explicito

### Negativas
- requiere mas diseño explicito en repositories y mapeos
- tiene una curva de adopcion mayor que un CRUD tipico con JPA
- obliga a pensar mejor boundaries entre domain, application e infrastructure

## Regla Operativa
Para el nuevo backend:
- el schema sera la fuente de verdad estructural
- los cambios estructurales viajaran via `Flyway`
- jOOQ sera la opcion por defecto para repositories del nuevo dominio
- JDBC podra usarse de forma puntual y justificada
- no se incorporaran nuevas features del dominio nuevo sobre JPA/Hibernate
