# ADR 001 - Adoptar Documentacion Tecnica Estructurada

## Estado
Aprobado

## Contexto
El proyecto no contaba con una base de documentacion tecnica estructurada por dominio, arquitectura y decisiones. Esto dificulta:
- mantener trazabilidad tecnica
- alinear backend y frontend en cambios de dominio
- sostener una evolucion hacia produccion y multi-tenancy

## Decision
Adoptar una estructura de documentacion tecnica dentro de `docs/` con las siguientes categorias iniciales:
- `adr/` para decisiones arquitectonicas registradas
- `architecture/` para arquitectura objetivo
- `data-model/` para dominio y modelo de datos

## Consecuencias
Positivas:
- las decisiones dejan de vivir solo en conversaciones o codigo
- se facilita el onboarding
- se mejora la coordinacion de cambios estructurales

Negativas:
- requiere disciplina para mantener la documentacion actualizada
