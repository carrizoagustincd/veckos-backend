# Estrategia De Entornos Backend

## Objetivo
Definir como debe configurarse y operarse `veckos-backend` en los ambientes `local`, `dev`, `test` y `prod`.

## Principios
- configuracion separada por ambiente
- secretos fuera del repositorio
- base de datos consistente con produccion cuando sea razonable
- migraciones versionadas y reproducibles
- observabilidad y seguridad acordes al ambiente

## Ambientes

### local
Uso:
- desarrollo individual
- debugging rapido
- pruebas exploratorias locales

Caracteristicas recomendadas:
- perfil Spring: `local`
- base de datos PostgreSQL local o contenedorizada
- datos seed opcionales
- logging mas verboso
- secretos via `.env.local` no versionado o variables de entorno locales

Objetivo:
- parecerse lo suficiente a produccion sin friccion excesiva para desarrolladores

### dev
Uso:
- ambiente compartido por el equipo
- integracion temprana
- validacion funcional continua

Caracteristicas recomendadas:
- perfil Spring: `dev`
- PostgreSQL administrado o contenedorizado persistente
- migraciones `Flyway` obligatorias
- datos de prueba controlados
- acceso restringido por equipo

Objetivo:
- detectar problemas de integracion antes de `test` o `prod`

### test
Uso:
- ejecucion automatica de tests
- validacion en CI
- pruebas de integracion reproducibles

Caracteristicas recomendadas:
- perfil Spring: `test`
- base efimera levantada por `Testcontainers`
- sin dependencias manuales a variables de base externas cuando no sean necesarias
- migraciones ejecutadas al iniciar tests

Objetivo:
- suite automatizada deterministica y autocontenida

### prod
Uso:
- entorno de produccion real

Caracteristicas recomendadas:
- perfil Spring: `prod`
- PostgreSQL administrado
- secretos via secret manager o variables protegidas
- logging acotado
- observabilidad completa
- backups y politicas de recuperacion

Objetivo:
- estabilidad, trazabilidad y seguridad operacional

## Configuracion De Spring
Se recomienda migrar hacia esta estructura:
- `application.yml`
- `application-local.yml`
- `application-dev.yml`
- `application-test.yml`
- `application-prod.yml`

### application.yml
Debe contener solo:
- configuracion comun segura
- defaults no sensibles
- parametros compartidos que no comprometan el entorno

### application-local.yml
Debe contener:
- datasource local
- logging de desarrollo
- banderas de conveniencia local

### application-dev.yml
Debe contener:
- datasource dev
- observabilidad media
- integraciones dev

### application-test.yml
Debe contener:
- configuracion enfocada en automatizacion
- integracion con contenedores o base efimera
- tiempos y logging aptos para CI

### application-prod.yml
Debe contener:
- configuracion endurecida
- sin defaults inseguros
- logs orientados a operacion

## Base De Datos

### Recomendacion
- PostgreSQL como base canonica en todos los entornos serios
- evitar divergencias entre `local` y `prod` en motor SQL si el costo es aceptable

### Migraciones
- adoptar `Flyway`
- cada cambio de schema debe viajar como migracion versionada
- evitar `spring.jpa.hibernate.ddl-auto=update` en `dev`, `test` y `prod`

## Secretos

## Politica
- nunca versionar secretos
- no commitear JWT secrets, passwords, tokens o credenciales de DB
- usar variables de entorno o secret manager

### Tipos de secretos esperados
- `JWT_SECRET`
- credenciales de PostgreSQL
- credenciales de servicios externos futuros

## Testing

### Unit tests
- reglas de negocio
- casos de uso
- validaciones

### Integration tests
- repositories
- seguridad
- controllers
- persistencia tenant-aware

### Herramienta recomendada
- `Testcontainers` con PostgreSQL

## Observabilidad
- `Spring Boot Actuator`
- health checks
- logs estructurados cuando el entorno lo requiera
- metricas basicas de aplicacion y datasource

## Entrega Continua

### Pipeline minimo recomendado
1. compilar
2. correr unit tests
3. correr integration tests
4. validar migraciones
5. empaquetar artefacto

### Regla operativa
- ningun deploy a `dev` o `prod` debe depender de cambios manuales de schema fuera de migraciones versionadas
