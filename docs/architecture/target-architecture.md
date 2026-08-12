# Arquitectura Objetivo Backend

## Objetivo
Definir la arquitectura objetivo de `veckos-backend` para soportar un dominio multi-tenant, facilitar mantenibilidad y preparar el sistema para produccion.

## Principios
- arquitectura modular por dominio, no solo por tipo tecnico
- entidades JPA no expuestas como contratos API
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
  - implementaciones JPA
  - repositories concretos
  - adaptadores externos

## Reglas Arquitectonicas
- controllers sin logica de negocio relevante
- reglas criticas de negocio en `application` y `domain`
- entidades JPA sin anotaciones o comportamiento orientado a serializacion HTTP
- uso de DTOs tipados para toda respuesta publica
- no usar `Object[]` en contratos internos ni externos
- no usar `@Data` en entidades persistentes

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
