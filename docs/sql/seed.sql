USE user_management_db;
GO

-- [CHANGED] 'GUEST' added to the list of roles
-- status and updated_at use their column defaults (ACTIVE, now)
INSERT INTO dbo.roles (name) VALUES ('ADMIN'), ('MANAGER'), ('USER'), ('GUEST');

INSERT INTO dbo.users (name, email, role_id) VALUES
('Alice Perera',       'alice.perera@example.com',    (SELECT id FROM dbo.roles WHERE name = 'ADMIN')),
('Nimal Fernando',     'nimal.fernando@example.com',  (SELECT id FROM dbo.roles WHERE name = 'MANAGER')),
('Kasun Silva',        'kasun.silva@example.com',     (SELECT id FROM dbo.roles WHERE name = 'USER')),
('Dilani Jayawardena', 'dilani.j@example.com',        (SELECT id FROM dbo.roles WHERE name = 'USER')),
('Ruwan Bandara',      'ruwan.bandara@example.com',   (SELECT id FROM dbo.roles WHERE name = 'USER'));
GO
