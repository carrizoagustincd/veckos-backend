# Documentacion Tecnica Backend

## Proposito
Esta carpeta centraliza la documentacion tecnica de `veckos-backend`.

Objetivos:
- dejar trazables las decisiones de arquitectura y dominio
- documentar el modelo de datos objetivo
- facilitar evolucion, onboarding y futuras auditorias

## Estructura
- `adr/`
  - decisiones arquitectonicas registradas (ADR)
- `architecture/`
  - arquitectura objetivo del backend
- `data-model/`
  - modelo de datos, DER logico y DDL conceptual
- `environments/`
  - estrategia de entornos, configuracion y operacion
- `persistence/`
  - estrategia de persistencia y acceso a datos

## Documentos Iniciales
- `adr/001-adoptar-documentacion-tecnica-estructurada.md`
- `adr/002-redisenar-modelo-de-datos-para-multi-tenancy.md`
- `adr/003-separar-entornos-y-secretos-por-ambiente.md`
- `adr/004-adoptar-postgresql-flyway-jooq-jdbc-para-persistencia.md`
- `architecture/target-architecture.md`
- `data-model/domain-model-overview.md`
- `data-model/logical-erd.md`
- `data-model/conceptual-ddl.md`
- `environments/environment-strategy.md`
- `persistence/persistence-strategy.md`

## Convenciones
- Todo el contenido se redacta en español.
- Nombres tecnicos del codigo, clases, endpoints, DTOs, tablas o comandos se mantienen en su idioma original cuando mejora la precision.
- Cada decision importante que afecte arquitectura, modelo de datos, integracion o despliegue debe registrarse como ADR.
