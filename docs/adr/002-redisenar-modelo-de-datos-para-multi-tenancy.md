# ADR 002 - Rediseniar El Modelo De Datos Para Multi-Tenancy

## Estado
Aprobado

## Contexto
El modelo actual del backend presenta ambiguedades de dominio, acoplamiento entre estados operativos y comerciales, y no fue diseñado para aislar datos por gimnasio.

Ademas, el objetivo del proyecto es evolucionar a un software multi-tenant apto para produccion, sin necesidad de compatibilidad con versiones anteriores del modelo.

## Decision
Adoptar un rediseño limpio del modelo de datos con estos principios:
- cada entidad de negocio del gimnasio debe ser tenant-scoped mediante `tenant_id`
- separar usuarios internos del gimnasio (`StaffUser`) de clientes/alumnos (`Member`)
- separar agenda recurrente (`ClassTemplate`) de sesiones concretas (`ClassSession`)
- separar estado operativo de suscripcion de estado de facturacion
- eliminar duplicacion semantica y cascadas destructivas sobre datos historicos
- modelar pagos, asistencia y suscripciones con reglas explicitamente auditables

La estrategia de tenancy inicial sera:
- una base de datos compartida
- un schema compartido
- aislamiento logico por `tenant_id`

## Consecuencias
Positivas:
- mejora de claridad y mantenibilidad
- base apta para escalar a SaaS multi-tenant
- mejor soporte para integridad y reportes

Negativas:
- requiere rediseñar contratos API y reorganizar modulos del backend
- aumenta el costo inicial de implementacion respecto de seguir parcheando el modelo actual
