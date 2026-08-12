# DER Logico Objetivo

## Convenciones
- `[1]` una instancia
- `[N]` muchas instancias
- `FK` foreign key
- las relaciones tenant-scoped asumen `tenant_id`

## Plataforma Y Tenancy
- `Tenant [1] -> [1] TenantSettings`
- `Tenant [1] -> [N] StaffUser`
- `Tenant [1] -> [N] Branch`
- `Tenant [1] -> [N] Member`
- `Tenant [1] -> [N] Plan`
- `Tenant [1] -> [N] ClassTemplate`
- `Tenant [1] -> [N] ClassSession`
- `Tenant [1] -> [N] PaymentAccount`
- `Tenant [1] -> [N] Payment`
- `Tenant [1] -> [N] Charge`

## Identidad Y Acceso
- `StaffUser [N] -> [N] Role` mediante `StaffUserRole`
- `Role [N] -> [N] Permission` mediante `RolePermission`

## Organizacion
- `Branch [1] -> [N] Room`
- `Branch [1] -> [N] Member` opcionalmente asociado por sede principal
- `Branch [1] -> [N] Plan` opcional si luego se limita disponibilidad por sede
- `Branch [1] -> [N] ClassTemplate`
- `Branch [1] -> [N] ClassSession`
- `Coach [1] -> [N] ClassTemplate`
- `Coach [1] -> [N] ClassSession`

## Miembros
- `Member [N] -> [N] MemberTag` mediante `MemberTagAssignment`
- `Member [1] -> [N] Subscription`
- `Member [1] -> [N] Attendance`
- `Member [1] -> [N] Payment`
- `Member [1] -> [N] Charge`
- `Member [1] -> [N] ClassEnrollment` opcional

## Planes Y Suscripciones
- `Plan [1] -> [N] PlanRule`
- `Plan [1] -> [N] Subscription`
- `Subscription [1] -> [N] SubscriptionSchedule`
- `Subscription [1] -> [N] SubscriptionPause`
- `Subscription [1] -> [N] Charge`
- `Subscription [1] -> [N] Payment` opcionalmente asociado

## Agenda Y Clases
- `ClassTemplate [1] -> [N] ClassSession`
- `ClassTemplate [1] -> [N] SubscriptionSchedule`
- `Room [1] -> [N] ClassTemplate`
- `Room [1] -> [N] ClassSession`
- `ClassSession [1] -> [N] Attendance`
- `ClassSession [1] -> [N] ClassEnrollment` opcional

## Asistencia
- `Attendance [N] -> [1] Member`
- `Attendance [N] -> [1] ClassSession`
- `Attendance [N] -> [1] Subscription` opcional
- `Attendance [1] -> [0..1] AttendancePolicyDecision`

## Cobros Y Pagos
- `PaymentAccount [1] -> [N] Payment`
- `Payment [N] -> [N] Charge` mediante `PaymentAllocation`
- `Payment [1] -> [N] Refund`

## Restricciones Criticas
- `StaffUser`: unico por `tenant_id + email`
- `Role`: unico por `tenant_id + code`
- `Branch`: unico por `tenant_id + code`
- `Member`: unico por `tenant_id + member_number`
- `Member`: recomendable unico por `tenant_id + document_type + document_number`
- `Plan`: unico por `tenant_id + code`
- `ClassSession`: unico por `class_template_id + session_date`
- `Attendance`: unico por `class_session_id + member_id`
- `SubscriptionSchedule`: unico por `subscription_id + class_template_id`
