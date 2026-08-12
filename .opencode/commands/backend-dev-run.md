---
description: Arranca el backend en perfil dev con SQLite para trabajo local.
agent: backend-spring
---

Ayuda con arranque local del backend usando el perfil `dev`.

Pasos:
1. Trabaja desde `veckos-backend/`.
2. Usa `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.
3. Explica cualquier error de arranque, migracion o datasource.
4. Si el usuario pide cambios para que arranque, implementalos y vuelve a verificar si es razonable.

Contexto adicional del repo:
- El perfil `dev` usa SQLite `jdbc:sqlite:veckos_gym_dev.db` y `create-drop`.

Pedido adicional del usuario: $ARGUMENTS
