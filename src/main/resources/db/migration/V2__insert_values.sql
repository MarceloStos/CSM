INSERT INTO csm.users (name, cpf, login, password_hash, email, status)
VALUES ('Administrador', '11122233344', 'admin', '$argon2id$v=19$m=16384,t=2,p=1$fusZvJJTmXNFufYfcHwU4g$LHo63vHRFtVXge92g60gyBQVE0JyqP304diTryBICqU', 'admin@csm.com', 1);

-- ============================================================================
-- SCRIPT DE POVOAMENTO INICIAL (SEED) - CSM IAM
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. APLICAÇÕES (SISTEMAS)
-- Cadastrando o próprio CSM para ele gerenciar a si mesmo.
-- ----------------------------------------------------------------------------
INSERT INTO csm.applications (id, client_id, client_secret_hash, name, acronym, redirect_uri, objective, is_published)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'csm-iam-client', '$argon2id$v=19$m=16384,t=2,p=1$placeholder_hash', 'Central de Segurança e Acessos', 'CSM', 'http://localhost:5173', 'Sistema central de gestão de identidades.', true);

-- ----------------------------------------------------------------------------
-- 2. PERMISSÕES
-- Mapeando o que pode ser feito dentro do CSM
-- ----------------------------------------------------------------------------
INSERT INTO csm.permissions (id, application_id, name, description)
VALUES
    ('a0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'USER_VIEW', 'Visualizar lista e detalhes de usuários'),
    ('a0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'USER_CREATE', 'Cadastrar novos usuários'),
    ('a0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'USER_UPDATE', 'Editar dados de usuários existentes'),
    ('a0000000-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 'USER_DELETE', 'Excluir ou inativar usuários'),
    ('a0000000-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 'REPORT_VIEW', 'Acessar relatórios e auditorias do sistema'),
    ('a0000000-0000-0000-0000-000000000006', '11111111-1111-1111-1111-111111111111', 'SYSTEM_MANAGE', 'Cadastrar e gerenciar novas aplicações no CSM');

-- ----------------------------------------------------------------------------
-- 3. PERFIS (ROLES)
-- Agrupando as permissões em 3 níveis hierárquicos
-- ----------------------------------------------------------------------------
INSERT INTO csm.roles (id, application_id, name, description)
VALUES
    ('b0000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'ADMIN', 'Administrador Geral do Sistema. Acesso total.'),
    ('b0000000-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'MANAGER', 'Gestor. Pode gerenciar usuários e ver relatórios, mas não gerencia sistemas.'),
    ('b0000000-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 'AUDITOR', 'Auditor. Acesso apenas de leitura aos dados e relatórios.');

-- ----------------------------------------------------------------------------
-- 4. VINCULANDO PERMISSÕES AOS PERFIS (ROLE_PERMISSIONS)
-- ----------------------------------------------------------------------------
-- ADMIN (Recebe todas as 6 permissões)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002'),
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000003'),
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000004'),
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000005'),
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000006');

-- MANAGER (Visualiza, Cria, Edita e vê relatórios)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003'),
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000005');

-- AUDITOR (Apenas leitura)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000001'),
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000005');

-- ----------------------------------------------------------------------------
-- 5. USUÁRIOS
-- Senha padrão para todos (Fictícia): $argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w
-- ----------------------------------------------------------------------------
INSERT INTO csm.users (id, name, cpf, login, email, password_hash, status)
VALUES
-- Usuário 1 (Admin
('c0000000-0000-0000-0000-000000000001', 'Administrador Chefe', '00000000001', 'adminadmin', 'admin@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w', 1),
-- Usuário 2 (Gestor)
('c0000000-0000-0000-0000-000000000002', 'Carlos Silveira', '11122233345', 'carlos.gestor', 'carlos@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w', 1),
-- Usuário 3 (Auditor)
('c0000000-0000-0000-0000-000000000003', 'Ana Beatriz', '55566677788', 'ana.auditoria', 'ana@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w', 1),
-- Usuário 4 (Operador base)
('c0000000-0000-0000-0000-000000000004', 'João Pedro', '99988877766', 'joao.pedro', 'joao@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w', 1),
-- Usuário 5 (Inativo para testes)
('c0000000-0000-0000-0000-000000000005', 'Usuário Desligado', '00011122233', 'user.desligado', 'desligado@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w', 0);

-- ----------------------------------------------------------------------------
-- 6. VINCULANDO USUÁRIOS AOS PERFIS (USER_ROLES)
-- ----------------------------------------------------------------------------
INSERT INTO csm.user_roles (user_id, role_id)
VALUES
    ('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001'), -- Admin -> ADMIN
    ('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000002'), -- Carlos -> MANAGER
    ('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000003'); -- Ana -> AUDITOR
-- (João Pedro e Usuário Desligado estão sem perfil de propósito, para você testar como o frontend reage a usuários sem permissão)