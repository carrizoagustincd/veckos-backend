---
description: Ejecuta la verificacion principal del backend con Maven wrapper.
agent: backend-spring
---

Verifica el backend de este repo.

Pasos:
1. Trabaja desde `veckos-backend/`.
2. Ejecuta `./mvnw test`.
3. Si falla por variables de entorno o base de datos, explicalo con precision y no inventes una causa.
4. Si el usuario pidio cambios antes de verificar, implementalos y luego corre la verificacion.

Contexto adicional del repo:
- Los tests no son totalmente autocontenidos; `application-test.properties` depende de `DATABASE_URL`, `PGUSER` y `PGPASSWORD`.

Pedido adicional del usuario: $ARGUMENTS
