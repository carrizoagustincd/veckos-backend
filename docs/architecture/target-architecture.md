# Arquitectura Objetivo Backend

## Objetivo
Definir la arquitectura objetivo de `veckos-backend` para soportar un dominio multi-tenant, facilitar mantenibilidad y preparar el sistema para produccion.

## Principios
- arquitectura modular por dominio, no solo por tipo tecnico
- el dominio no debe acoplarse a entidades JPA ni a contratos de persistencia implicitos
- casos de uso explicitos en capa de aplicacion
- aislamiento estricto por `tenant_id`
- trazabilidad de decisiones mediante ADRs

## Estructura Objetivo
Se recomienda reorganizar el backend por modulos funcionales:
- `tenancy`
- `identity-access`
- `organization`
- `members`
- `plans`
- `subscriptions`
- `scheduling`
- `attendance`
- `billing`
- `reporting`
- `shared`

## Capas Por Modulo
Cada modulo deberia separar responsabilidades en capas internas:
- `api`
  - controllers
  - request/response DTOs
- `application`
  - casos de uso
  - servicios de aplicacion
  - comandos/queries
- `domain`
  - entidades de dominio
  - value objects
  - reglas de negocio
- `infrastructure`
  - repositories concretos
  - implementaciones con jOOQ
  - implementaciones puntuales con JDBC
  - adaptadores externos

## Reglas Arquitectonicas
- controllers sin logica de negocio relevante
- reglas criticas de negocio en `application` y `domain`
- no exponer detalles de persistencia en contratos API
- uso de DTOs tipados para toda respuesta publica
- no usar `Object[]` en contratos internos ni externos
- no basar el nuevo dominio en entidades JPA/Hibernate

## Multi-Tenancy
### Estrategia Inicial
- single database
- shared schema
- `tenant_id` obligatorio en toda entidad tenant-scoped

### Requisitos Arquitectonicos
- todo acceso a datos tenant-scoped debe filtrar por `tenant_id`
- el contexto de tenant debe resolverse en autenticacion/middleware
- no deben existir queries de negocio sin conciencia de tenant

## Persistencia Y Evolucion De Schema
- usar PostgreSQL como base principal objetivo
- introducir `Flyway` para migraciones versionadas
- prohibir dependencia de `ddl-auto=update` fuera de desarrollo local controlado
- usar `jOOQ` como tecnologia principal de acceso a datos en el nuevo dominio
- permitir `JDBC` en casos puntuales donde aporte simplicidad o performance con justificacion tecnica

## Errores Y Observabilidad
- centralizar errores con `@ControllerAdvice`
- definir error responses estandarizados
- agregar `Spring Boot Actuator`
- establecer logging diferenciado por entorno

## Testing Objetivo
- unit tests de casos de uso y reglas de dominio
- integration tests de repositories y controllers
- tests de seguridad
- uso recomendado de `Testcontainers` para PostgreSQL
