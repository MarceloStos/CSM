-- INSERT INTO csm.users (ID, name, cpf, login, password_hash, email, status)
-- VALUES ('9484c6ea-b228-4bfb-a128-6cd26208a840', 'Administrador', '11122233344', 'admin', '$argon2id$v=19$m=16384,t=2,p=1$fusZvJJTmXNFufYfcHwU4g$LHo63vHRFtVXge92g60gyBQVE0JyqP304diTryBICqU', 'admin@csm.com', 1);

-- ============================================================================
-- SCRIPT DE POVOAMENTO INICIAL (SEED) - CSM IAM
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. APLICAÇÕES (SISTEMAS)
-- Cadastrando o próprio CSM para ele gerenciar a si mesmo.
-- ----------------------------------------------------------------------------
INSERT INTO csm.applications (id, client_id, client_secret_hash, name, acronym, redirect_uri, objective, is_published)
VALUES
    ('2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'csm-iam-client', '$argon2id$v=19$m=16384,t=2,p=1$placeholder_hash', 'Central de Segurança e Acessos', 'CSM', 'http://localhost:5173', 'Sistema central de gestão de identidades.', true);

-- ----------------------------------------------------------------------------
-- 2. PERMISSÕES
-- Mapeando o que pode ser feito dentro do CSM
-- ----------------------------------------------------------------------------
INSERT INTO csm.permissions (id, application_id, name, description)
VALUES
    ('7e8d9c0b-1a2f-3b4c-5d6e-7f8a9b0c1d2e','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_VIEW','Visualizar lista e detalhes de usuários'),
    ('a1b2c3d4-e5f6-4789-9a0b-1c2d3e4f5a6b','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_CREATE','Cadastrar novos usuários'),
    ('5d4c3b2a-1f0e-9d8c-7b6a-5e4f3d2c1b0a','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_UPDATE','Editar dados de usuários existentes'),
    ('8c7d6e5f-4a3b-2c1d-0f9e-8d7c6b5a4e3f','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_DELETE','Excluir ou inativar usuários'),
    ('6fd1c4d7-8437-4c3c-8974-19255db111f2','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_RESET_PASSWORD','Redefinir senha de usuários'),
    ('3d768475-281f-4406-a8a1-ed6d6666a231','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','USER_UNLOCK','Desbloquear contas travadas'),
    ('272d5a16-9fb7-4864-a457-5b3ae577548b','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','APPLICATION_VIEW','Visualizar sistemas e credenciais básicas'),
    ('a264896e-3bdd-48be-a83c-819513217557','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','APPLICATION_CREATE','Registrar novas aplicações (Gera credenciais)'),
    ('b0d11842-de7d-49fb-a0c1-b99d02cbfdcb','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','APPLICATION_UPDATE','Alterar metadados e URIs de sistemas'),
    ('965685d7-425d-4510-96ae-13fb93581035','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','APPLICATION_DELETE','Inativar aplicações integradas'),
    ('d7417a5a-aab2-4f1f-8767-571ce07f9011','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','ROLE_VIEW','Visualizar os perfis de acesso cadastrados'),
    ('87a5e8f6-05e7-43d9-bb5e-958f1cf11d9a','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','ROLE_CREATE','Criar novos perfis'),
    ('730455f9-555a-4a56-913f-f63912bc4c8d','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','ROLE_UPDATE','Editar nomes e gerenciar vínculos de perfis'),
    ('dba5a9bd-5e1c-4b4a-8964-58f32893e562','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','ROLE_DELETE','Remover perfis obsoletos'),
    ('9de8e9d4-4d3b-402a-9a3f-affe70752e0b','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','PERMISSION_VIEW','Visualizar o catálogo de permissões'),
    ('47c1ae8a-27d8-4478-b19a-a198262682ee','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','PERMISSION_CREATE','Cadastrar novas permissões atômicas'),
    ('0f9e8d7c-6b5a-4c3d-2e1f-0a9b8c7d6e5f','2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f','AUDIT_VIEW','Acessar relatórios e auditorias do sistema');

-- ----------------------------------------------------------------------------
-- 3. PERFIS (ROLES)
-- Agrupando as permissões em 5 níveis hierárquicos
-- ----------------------------------------------------------------------------
INSERT INTO csm.roles (id, application_id, name, description)
VALUES
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', '2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'AUDITOR','Auditor. Acesso apenas de leitura aos dados e relatórios.'),
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', '2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'MANAGER','DevOps. Integra e gerencia novos sistemas.'),
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'SECURITY ADMIN','Gestor de Identidades. Gerencia usuários e perfis.'),
    ('fa657d29-4dbe-40c6-8cc8-195f6c30d78d', '2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'HELPDESK','Suporte N1. Reset de senhas e desbloqueio.'),
    ('abd515a4-2427-46db-80f7-b2f8f92e9ccc', '2f4b6a7c-8d9e-4c1b-9a0f-7e6d5c4b3a2f', 'SUPER ADMIN','Controle total sobre o ecossistema.');

-- ----------------------------------------------------------------------------
-- 4. VINCULANDO PERMISSÕES AOS PERFIS (ROLE_PERMISSIONS)
-- ----------------------------------------------------------------------------
-- SUPER ADMIN (Recebe todas as permissões cadastradas)
INSERT INTO csm.role_permissions (role_id, permission_id)
SELECT 'abd515a4-2427-46db-80f7-b2f8f92e9ccc', id FROM csm.permissions;

-- SECURITY ADMIN (Foco em Pessoas e Perfis)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '7e8d9c0b-1a2f-3b4c-5d6e-7f8a9b0c1d2e'), -- USER_VIEW
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', 'a1b2c3d4-e5f6-4789-9a0b-1c2d3e4f5a6b'), -- USER_CREATE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '5d4c3b2a-1f0e-9d8c-7b6a-5e4f3d2c1b0a'), -- USER_UPDATE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '8c7d6e5f-4a3b-2c1d-0f9e-8d7c6b5a4e3f'), -- USER_DELETE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '3d768475-281f-4406-a8a1-ed6d6666a231'), -- USER_UNLOCK
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', 'd7417a5a-aab2-4f1f-8767-571ce07f9011'), -- ROLE_VIEW
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '87a5e8f6-05e7-43d9-bb5e-958f1cf11d9a'), -- ROLE_CREATE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '730455f9-555a-4a56-913f-f63912bc4c8d'), -- ROLE_UPDATE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', 'dba5a9bd-5e1c-4b4a-8964-58f32893e562'), -- ROLE_DELETE
    ('6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c', '9de8e9d4-4d3b-402a-9a3f-affe70752e0b'); -- PERMISSION_VIEW

-- MANAGER (Foco em Sistemas e Integrações)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', '272d5a16-9fb7-4864-a457-5b3ae577548b'), -- APPLICATION_VIEW
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', 'a264896e-3bdd-48be-a83c-819513217557'), -- APPLICATION_CREATE
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', 'b0d11842-de7d-49fb-a0c1-b99d02cbfdcb'), -- APPLICATION_UPDATE
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', '965685d7-425d-4510-96ae-13fb93581035'), -- APPLICATION_DELETE
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', 'd7417a5a-aab2-4f1f-8767-571ce07f9011'), -- ROLE_VIEW
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', '9de8e9d4-4d3b-402a-9a3f-affe70752e0b'), -- PERMISSION_VIEW
    ('4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b', '47c1ae8a-27d8-4478-b19a-a198262682ee'); -- PERMISSION_CREATE

-- HELPDESK (Suporte Básico)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('fa657d29-4dbe-40c6-8cc8-195f6c30d78d', '7e8d9c0b-1a2f-3b4c-5d6e-7f8a9b0c1d2e'), -- USER_VIEW
    ('fa657d29-4dbe-40c6-8cc8-195f6c30d78d', '6fd1c4d7-8437-4c3c-8974-19255db111f2'), -- USER_RESET_PASSWORD
    ('fa657d29-4dbe-40c6-8cc8-195f6c30d78d', '3d768475-281f-4406-a8a1-ed6d6666a231'); -- USER_UNLOCK

-- AUDITOR (Apenas Leitura)
INSERT INTO csm.role_permissions (role_id, permission_id)
VALUES
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', '7e8d9c0b-1a2f-3b4c-5d6e-7f8a9b0c1d2e'), -- USER_VIEW
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', '272d5a16-9fb7-4864-a457-5b3ae577548b'), -- APPLICATION_VIEW
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 'd7417a5a-aab2-4f1f-8767-571ce07f9011'), -- ROLE_VIEW
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', '9de8e9d4-4d3b-402a-9a3f-affe70752e0b'), -- PERMISSION_VIEW
    ('9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', '0f9e8d7c-6b5a-4c3d-2e1f-0a9b8c7d6e5f'); -- AUDIT_VIEW

-- ----------------------------------------------------------------------------
-- 5. USUÁRIOS
-- Senha padrão para todos (Fictícia): $argon2id$v=19$m=16384,t=2,p=1$c2FsdGdlbmVyaWNv$S3uaK9sY3O5U3Q2M3X1T5w
-- ----------------------------------------------------------------------------
INSERT INTO csm.users (id, name, cpf, login, email, password_hash, status)
VALUES
    -- Usuário 0 (Super Admin)
    ('9484c6ea-b228-4bfb-a128-6cd26208a840', 'Super Administrador', '11122233344', 'admin', 'admin@csm.com', '$argon2id$v=19$m=16384,t=2,p=1$fusZvJJTmXNFufYfcHwU4g$LHo63vHRFtVXge92g60gyBQVE0JyqP304diTryBICqU', 1),
    -- Usuário 1 (Admin)
    ('f1e2d3c4-b5a6-4789-8c7d-9e0f1a2b3c4d', 'Administrador de Segurança', '00000000001', 'adminadmin', 'admin@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$Q9aPbTNFk0Kgyb3w1eIhvw$vhmbRTWsJOhW4dHWqOve8gipIht5nIBZj5MhfrFF7ZY', 1),
    -- Usuário 2 (Gestor)
    ('c3f2a9d4-7b1e-4c2a-9f8d-1a2b3c4d5e6f', 'Carlos Silveira', '11122233345', 'carlos.gestor', 'carlos@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$zS3h1rU0B1dBY4d2qgV0ag$qwduqqu6DGUxZhj2k3fab9SZKAxOFepnmi0Q9RKN4Y4', 1),
    -- Usuário 3 (Auditor)
    ('8a7b6c5d-4e3f-2a1b-9c8d-7e6f5a4b3c2d', 'Ana Beatriz', '55566677788', 'ana.auditoria', 'ana@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$FgNjyr1XFKC3FPscPjh3yQ$SaP6wXapB2jJSaIiTHcOIIOHUDw0DT0O5aFUanv3r1E', 1),
    -- Usuário 4 (Operador base)
    ('9d8c7b6a-5e4f-3a2b-1c0d-9f8e7d6c5b4a', 'João Pedro', '99988877766', 'joao.pedro', 'joao@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$mjA9CSsTSRti5Ivz9au63g$2UT/dluPfyPL7+7qv7kf7YuJUaC2jTGEmZ+g0e3hX/A', 1),
    -- Usuário 5 (Inativo para testes)
    ('0a1b2c3d-4e5f-6789-8a7b-9c0d1e2f3a4b', 'Usuário Desligado', '00011122233', 'user.desligado', 'desligado@csm.local', '$argon2id$v=19$m=16384,t=2,p=1$vUeo94ETMtQ/LzYYbl0PLA$YW67BCBBnStNKm2r6gf9rYMIiGYMlvMyAAF1K45UsF8', 0);

-- ----------------------------------------------------------------------------
-- 6. VINCULANDO USUÁRIOS AOS PERFIS (USER_ROLES)
-- ----------------------------------------------------------------------------
INSERT INTO csm.user_roles (user_id, role_id)
VALUES
    ('f1e2d3c4-b5a6-4789-8c7d-9e0f1a2b3c4d', '6e5f4d3c-2b1a-0f9e-8d7c-6b5a4e3f2d1c'), -- Admin -> SECURITY ADMIN
    ('c3f2a9d4-7b1e-4c2a-9f8d-1a2b3c4d5e6f', '4e3f2d1c-0b9a-8d7c-6b5a-4e3f2d1c0a9b'), -- Carlos -> MANAGER
    ('8a7b6c5d-4e3f-2a1b-9c8d-7e6f5a4b3c2d', '9a0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d'), -- Ana -> AUDITOR
    ('9484c6ea-b228-4bfb-a128-6cd26208a840', 'abd515a4-2427-46db-80f7-b2f8f92e9ccc'), -- admin -> SUPER ADMIN
    ('9d8c7b6a-5e4f-3a2b-1c0d-9f8e7d6c5b4a', 'fa657d29-4dbe-40c6-8cc8-195f6c30d78d'); -- suporte -> HELPDESK
-- (João Pedro e Usuário Desligado estão sem perfil de propósito, para você testar como o frontend reage a usuários sem permissão)