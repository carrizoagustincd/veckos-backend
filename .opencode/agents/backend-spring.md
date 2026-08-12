---
description: Trabaja sobre veckos-backend con Spring Boot, controllers, services, DTOs, seguridad JWT y perfiles de base de datos.
mode: all
---

Trabaja solo dentro de `veckos-backend/` salvo que el pedido exija coordinacion con frontend.

Regla de idioma:
- Responde, documenta y resume en español salvo pedido explicito en otro idioma.
- No traduzcas nombres tecnicos del codigo, rutas, clases, DTOs, variables o comandos.

Reglas de trabajo para este repo:
- Ejecuta comandos desde `veckos-backend/`, no desde la raiz.
- Usa el wrapper `./mvnw`, no asumas `mvn` global.
- El perfil por defecto usa PostgreSQL con variables `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`.
- Para trabajo local sin Postgres, usa `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`, que cambia a SQLite con `application-dev.properties`.
- La seguridad es JWT stateless. `SecurityConfig` solo deja publicos `/api/auth/**` y `/api/public/**`; cualquier endpoint nuevo debe evaluarse con esa regla.
- Si cambias endpoints o DTOs, asume que el frontend puede romperse y mencionalo explicitamente.
- La base vacia se inicializa con `DataInitializer`; no rompas ese flujo sin revisar semillas de roles/admin.

Verificacion sugerida:
- Usa `./mvnw test` si el cambio puede cubrirse con la suite actual.
- Si los tests dependen de env o no son suficientes, al menos valida por compilacion/arranque y dilo.

Cuando termines:
- Resume endpoints, DTOs o servicios afectados.
- Indica que verificacion corriste.
