# ADR 003 - Separar Entornos Y Secretos Por Ambiente

## Estado
Aprobado

## Contexto
El backend actual mezcla configuraciones sensibles y operativas de forma poco controlada, con dependencias de variables de entorno incompletas, perfiles inconsistentes y secretos versionados en el repositorio.

Para evolucionar a produccion y soportar entregas continuas, el backend necesita una estrategia de entornos clara y reproducible.

## Decision
Adoptar una estrategia de configuracion por ambientes con estos principios:
- `local`, `dev`, `test` y `prod` como entornos diferenciados
- secretos siempre fuera del repositorio
- PostgreSQL como base objetivo para `dev`, `test` y `prod`
- `Flyway` como mecanismo de evolucion de schema
- `Testcontainers` como base recomendada para tests de integracion del backend

## Consecuencias
Positivas:
- mayor seguridad operacional
- despliegues mas predecibles
- entornos alineados con CI/CD y produccion

Negativas:
- aumenta la inversion inicial en infraestructura y automatizacion
