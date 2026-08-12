# Modelo De Dominio Objetivo

## Objetivo
Este documento resume el modelo de dominio objetivo para `veckos-backend`, pensado como base de una plataforma multi-tenant para gimnasios.

## Contextos Principales
- Plataforma y tenancy
- Identidad y acceso
- Organizacion
- Miembros
- Planes y suscripciones
- Agenda y clases
- Asistencia
- Cobros y pagos

## Entidades Principales

### Plataforma Y Tenancy
- `Tenant`
- `TenantSettings`

### Identidad Y Acceso
- `StaffUser`
- `Role`
- `Permission`
- `StaffUserRole`
- `RolePermission`

### Organizacion
- `Branch`
- `Room`
- `Coach`

### Miembros
- `Member`
- `MemberTag`
- `MemberTagAssignment`

### Planes Y Suscripciones
- `Plan`
- `PlanRule`
- `Subscription`
- `SubscriptionSchedule`
- `SubscriptionPause`

### Agenda Y Clases
- `ClassTemplate`
- `ClassSession`
- `ClassEnrollment` opcional

### Asistencia
- `Attendance`
- `AttendancePolicyDecision` opcional

### Cobros Y Pagos
- `PaymentAccount`
- `Charge`
- `Payment`
- `PaymentAllocation`
- `Refund`

## Principios De Modelado
- separar usuarios internos (`StaffUser`) de clientes (`Member`)
- separar definiciones recurrentes (`ClassTemplate`) de instancias concretas (`ClassSession`)
- separar estado operativo de suscripcion de estado de facturacion
- evitar duplicacion semantica de campos derivables
- no permitir borrados destructivos sobre historicos financieros ni de asistencia
