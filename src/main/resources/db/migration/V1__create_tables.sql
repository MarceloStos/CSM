-- ============================================================================
-- CSM SCHEMA — IDENTITY AND ACCESS MANAGEMENT (IAM) DATABASE STRUCTURE
-- ============================================================================

-- Clean up existing tables to avoid conflict during creation
DROP TABLE IF EXISTS csm.user_roles CASCADE;
DROP TABLE IF EXISTS csm.role_permissions CASCADE;
DROP TABLE IF EXISTS csm.permissions CASCADE;
DROP TABLE IF EXISTS csm.roles CASCADE;
DROP TABLE IF EXISTS csm.sistemas CASCADE;
DROP TABLE IF EXISTS csm.applications CASCADE;
DROP TABLE IF EXISTS csm.users CASCADE;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE
-- Stores all personal and authentication data of the users.
-- ----------------------------------------------------------------------------
create table csm.users
(
    -- Primary Key
    id                    uuid        default gen_random_uuid() primary key,

    -- Personal Data
    name                  varchar(100)                          not null,
    cpf                   varchar(11)                           not null unique,

    -- Credentials and Authentication
    login                 varchar(100)                          NOT NULL UNIQUE,
    email                 varchar(150) UNIQUE,
    password_hash         text                                  NOT NULL,

    -- Account Security and Brute Force Protection
    failed_attempts       int2        DEFAULT 0                 NOT NULL,
    blocked_until         timestamptz                           NULL,
    mfa_secret            varchar(128)                          NULL,
    force_password_change boolean     DEFAULT false             NOT NULL,

    -- Integration Metadata and Active Directory/LDAP linkage
    objectguid            varchar(64) UNIQUE,
    registration_number   int4,                                           --
    status                int2        DEFAULT 1                 NOT NULL, -- 1: Active, 0: Suspended

    -- Preferencias / UI
    hidden_tutorial       boolean     default true,

    -- Relationship References
    unit_id               int4,
    contract_id           int4,
    photo_id              int4,

    -- Audit & Traceability
    created_at            timestamptz default current_timestamp not null,
    updated_at            timestamptz default current_timestamp not null,
    deleted_at            timestamptz                           null,
    last_login            timestamptz                           null

    -- Relationship Constaints
);

-- ----------------------------------------------------------------------------
-- 2. APPLICATIONS TABLE
-- Governance catalog and Single Sign-On (SSO) credentials for client apps.
-- ----------------------------------------------------------------------------
create table csm.applications
(
    -- SSO Identity & Security Credentials (SSO / IAM)
    id                 uuid        default gen_random_uuid() primary key,
    client_id          varchar(80) unique                    not null,
    client_secret_hash text                                  not null,

    -- Visual & Catalog Metadata
    name               varchar(200)                          not null,
    acronym            varchar(30)                           not null,

    -- Routing & OAuth2 Requirements
    url                varchar(255),
    redirect_uri       varchar(255)                          not null,

    -- Access Control & Status
    status             int2        default 1                 not null,
    is_published       boolean     default false             not null,

    -- Corporate Governance Metadatas
    objective          text,
    notes              text,
    requester          varchar(255),
    project_start_date date,
    git_namespace      varchar(200),

    -- Audit & Traceability
    created_at         timestamptz default current_timestamp not null,
    updated_at         timestamptz default current_timestamp not null,
    deactivated_at     timestamptz                           null
);

-- ----------------------------------------------------------------------------
-- 3. ROLES TABLE
-- Access catalogs available inside each application (RBAC).
-- ----------------------------------------------------------------------------
CREATE TABLE csm.roles
(
    id             uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    application_id uuid                                  NOT NULL,

    name           varchar(50)                           NOT NULL, -- e.g., 'ADMIN', 'OPERATOR', 'VIEWER'
    description    varchar(255),
    status         int2        DEFAULT 1                 NOT NULL,

    created_at     timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at     timestamptz DEFAULT current_timestamp NOT NULL,

    -- Relationship Constraints
    CONSTRAINT fk_roles_applications FOREIGN KEY (application_id) REFERENCES csm.applications (id) ON DELETE CASCADE,
    -- Prevent duplicate roles inside the exact same application
    CONSTRAINT un_role_application UNIQUE (application_id, name)
);

-- ----------------------------------------------------------------------------
-- 4. USER_ROLES TABLE
-- Many-to-Many junction table linking Users to their granted Application Roles.
-- ----------------------------------------------------------------------------
CREATE TABLE csm.user_roles
(
    user_id     uuid                                  NOT NULL,
    role_id     uuid                                  NOT NULL,

    assigned_at timestamptz DEFAULT current_timestamp NOT NULL,

    -- Composite Primary Key prevents duplicate entries
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES csm.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES csm.roles (id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 5. PERMISSIONS TABLE
-- Granular technical actions allowed inside each application.
-- ----------------------------------------------------------------------------
CREATE TABLE csm.permissions
(
    id             uuid        DEFAULT gen_random_uuid() PRIMARY KEY,
    application_id uuid                                  NOT NULL,

    name           varchar(100)                          NOT NULL, -- e.g., 'SYSTEM_DISABLE', 'USER_CREATE'
    description    varchar(255),

    created_at     timestamptz DEFAULT current_timestamp NOT NULL,

    -- Relationship Constraints
    CONSTRAINT fk_permissions_applications FOREIGN KEY (application_id) REFERENCES csm.applications (id) ON DELETE CASCADE,
    -- Prevent duplicate permissions inside the exact same application
    CONSTRAINT un_permission_application UNIQUE (application_id, name)
);

-- ----------------------------------------------------------------------------
-- 6. ROLE_PERMISSIONS TABLE
-- Many-to-Many junction table linking Roles to their granted Permissions.
-- ----------------------------------------------------------------------------
CREATE TABLE csm.role_permissions
(
    role_id       uuid                                  NOT NULL,
    permission_id uuid                                  NOT NULL,

    assigned_at   timestamptz DEFAULT current_timestamp NOT NULL,

    -- Composite Primary Key prevents duplicate entries
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES csm.roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES csm.permissions (id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 7. CSM_AUDIT_LOGS TABLE
-- Stores immutable audit events, tracking actions, HTTP context and JSON payloads.
-- ----------------------------------------------------------------------------
CREATE TABLE csm.audit_logs
(
    -- Primary Key
    id               uuid        DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Event Temporal Data & Traceability
    timestamp        timestamptz                           NOT NULL,
    system_origin    varchar(100)                          NOT NULL,
    trace_id         varchar(64),

    -- Event Categorization
    log_level        varchar(20)                           NOT NULL,
    event_type       varchar(50)                           NOT NULL,
    action           varchar(100)                          NOT NULL,
    status           varchar(20),

    -- User Context
    user_id          uuid,
    username         varchar(150),

    -- HTTP Context
    ip_address       varchar(45),
    http_method      varchar(10),
    endpoint         varchar(255),

    -- Payloads & Error Details
    payload_request  jsonb,
    payload_response jsonb,
    error_message    text,
    stack_trace      text
);