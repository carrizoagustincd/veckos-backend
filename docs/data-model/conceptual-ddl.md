# DDL Conceptual Objetivo

## Objetivo
Este documento describe el diseño conceptual de tablas y restricciones del backend multi-tenant. No es un script ejecutable final; sirve como contrato tecnico previo a la implementacion.

## Reglas Globales
- toda tabla tenant-scoped debe incluir `tenant_id not null`
- toda tabla debe incluir columnas de auditoria segun corresponda: `created_at`, `updated_at`
- evitar `delete cascade` sobre historicos financieros, sesiones o asistencias

## Tablas Base

### tenant
- `id` PK
- `slug` varchar unique not null
- `business_name` varchar not null
- `display_name` varchar not null
- `tax_id` varchar null
- `status` varchar not null
- `timezone` varchar not null
- `currency_code` varchar not null
- `country_code` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null

### tenant_settings
- `id` PK
- `tenant_id` FK unique not null
- `default_membership_grace_days` integer not null
- `attendance_tolerance_minutes` integer not null
- `allow_multiple_active_subscriptions` boolean not null
- `require_payment_before_attendance` boolean not null
- `default_cancellation_policy` jsonb null
- `created_at` timestamp not null
- `updated_at` timestamp not null

## Identidad Y Acceso

### staff_user
- `id` PK
- `tenant_id` FK not null
- `email` varchar not null
- `username` varchar null
- `password_hash` varchar not null
- `first_name` varchar not null
- `last_name` varchar not null
- `phone` varchar null
- `status` varchar not null
- `last_login_at` timestamp null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`tenant_id`, `email`)
- optional unique (`tenant_id`, `username`)

### role
- `id` PK
- `tenant_id` FK not null
- `code` varchar not null
- `name` varchar not null
- `description` varchar null
- `is_system` boolean not null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`tenant_id`, `code`)

### permission
- `id` PK
- `code` varchar unique not null
- `name` varchar not null
- `description` varchar null

### staff_user_role
- `staff_user_id` FK not null
- `role_id` FK not null
- primary key (`staff_user_id`, `role_id`)

### role_permission
- `role_id` FK not null
- `permission_id` FK not null
- primary key (`role_id`, `permission_id`)

## Organizacion

### branch
- `id` PK
- `tenant_id` FK not null
- `code` varchar not null
- `name` varchar not null
- `address_line` varchar not null
- `city` varchar not null
- `state` varchar null
- `country_code` varchar not null
- `phone` varchar null
- `email` varchar null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`tenant_id`, `code`)

### room
- `id` PK
- `tenant_id` FK not null
- `branch_id` FK not null
- `code` varchar not null
- `name` varchar not null
- `capacity` integer not null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`branch_id`, `code`)

### coach
- `id` PK
- `tenant_id` FK not null
- `staff_user_id` FK null
- `first_name` varchar not null
- `last_name` varchar not null
- `email` varchar null
- `phone` varchar null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null

## Miembros

### member
- `id` PK
- `tenant_id` FK not null
- `branch_id` FK null
- `member_number` varchar not null
- `first_name` varchar not null
- `last_name` varchar not null
- `document_type` varchar not null
- `document_number` varchar not null
- `tax_id` varchar null
- `birth_date` date null
- `gender` varchar null
- `email` varchar null
- `phone` varchar null
- `emergency_contact_name` varchar null
- `emergency_contact_phone` varchar null
- `medical_notes` text null
- `joined_at` timestamp not null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- `archived_at` timestamp null
- unique (`tenant_id`, `member_number`)
- recommended unique (`tenant_id`, `document_type`, `document_number`)

### member_tag
- `id` PK
- `tenant_id` FK not null
- `code` varchar not null
- `name` varchar not null
- unique (`tenant_id`, `code`)

### member_tag_assignment
- `member_id` FK not null
- `tag_id` FK not null
- primary key (`member_id`, `tag_id`)

## Planes Y Suscripciones

### plan
- `id` PK
- `tenant_id` FK not null
- `code` varchar not null
- `name` varchar not null
- `description` text null
- `status` varchar not null
- `billing_period_unit` varchar not null
- `billing_period_count` integer not null
- `base_price` numeric not null
- `currency_code` varchar not null
- `attendance_mode` varchar not null
- `max_weekly_sessions` integer null
- `max_monthly_sessions` integer null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`tenant_id`, `code`)

### plan_rule
- `id` PK
- `plan_id` FK not null
- `rule_type` varchar not null
- `rule_value_json` jsonb not null

### subscription
- `id` PK
- `tenant_id` FK not null
- `member_id` FK not null
- `plan_id` FK not null
- `branch_id` FK null
- `started_at` timestamp not null
- `ends_at` timestamp null
- `cancelled_at` timestamp null
- `status` varchar not null
- `billing_status` varchar not null
- `price_snapshot` numeric not null
- `currency_code` varchar not null
- `notes` text null
- `created_at` timestamp not null
- `updated_at` timestamp not null

### subscription_schedule
- `id` PK
- `subscription_id` FK not null
- `class_template_id` FK not null
- `created_at` timestamp not null
- unique (`subscription_id`, `class_template_id`)

### subscription_pause
- `id` PK
- `subscription_id` FK not null
- `start_date` date not null
- `end_date` date not null
- `reason` text null
- `created_at` timestamp not null

## Agenda Y Clases

### class_template
- `id` PK
- `tenant_id` FK not null
- `branch_id` FK not null
- `room_id` FK null
- `coach_id` FK null
- `code` varchar not null
- `name` varchar not null
- `description` text null
- `day_of_week` varchar not null
- `start_time` time not null
- `end_time` time not null
- `capacity` integer not null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`tenant_id`, `code`)

### class_session
- `id` PK
- `tenant_id` FK not null
- `class_template_id` FK not null
- `branch_id` FK not null
- `room_id` FK null
- `coach_id` FK null
- `session_date` date not null
- `starts_at` timestamp not null
- `ends_at` timestamp not null
- `capacity_snapshot` integer not null
- `status` varchar not null
- `notes` text null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`class_template_id`, `session_date`)

### class_enrollment
- `id` PK
- `tenant_id` FK not null
- `class_session_id` FK not null
- `member_id` FK not null
- `subscription_id` FK null
- `status` varchar not null
- `booked_at` timestamp not null
- `cancelled_at` timestamp null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`class_session_id`, `member_id`)

## Asistencia

### attendance
- `id` PK
- `tenant_id` FK not null
- `class_session_id` FK not null
- `member_id` FK not null
- `subscription_id` FK null
- `attendance_status` varchar not null
- `check_in_at` timestamp null
- `recorded_at` timestamp not null
- `recorded_by_staff_user_id` FK null
- `notes` text null
- `created_at` timestamp not null
- `updated_at` timestamp not null
- unique (`class_session_id`, `member_id`)

### attendance_policy_decision
- `id` PK
- `attendance_id` FK unique not null
- `was_subscription_active` boolean not null
- `was_schedule_allowed` boolean not null
- `was_payment_ok` boolean not null
- `decision_reason` text null
- `created_at` timestamp not null

## Cobros Y Pagos

### payment_account
- `id` PK
- `tenant_id` FK not null
- `branch_id` FK null
- `account_type` varchar not null
- `provider_name` varchar not null
- `display_name` varchar not null
- `account_holder_name` varchar null
- `account_reference` varchar null
- `cbu` varchar null
- `alias` varchar null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null

### charge
- `id` PK
- `tenant_id` FK not null
- `member_id` FK not null
- `subscription_id` FK null
- `charge_type` varchar not null
- `description` text not null
- `amount` numeric not null
- `currency_code` varchar not null
- `due_date` date not null
- `status` varchar not null
- `created_at` timestamp not null
- `updated_at` timestamp not null

### payment
- `id` PK
- `tenant_id` FK not null
- `member_id` FK not null
- `subscription_id` FK null
- `payment_account_id` FK null
- `payment_method` varchar not null
- `payment_direction` varchar not null
- `amount` numeric not null
- `currency_code` varchar not null
- `paid_at` timestamp not null
- `external_reference` varchar null
- `description` text null
- `status` varchar not null
- `recorded_by_staff_user_id` FK null
- `created_at` timestamp not null
- `updated_at` timestamp not null

### payment_allocation
- `id` PK
- `payment_id` FK not null
- `charge_id` FK not null
- `allocated_amount` numeric not null
- `created_at` timestamp not null

### refund
- `id` PK
- `tenant_id` FK not null
- `payment_id` FK not null
- `amount` numeric not null
- `reason` text null
- `refunded_at` timestamp not null
- `created_at` timestamp not null
