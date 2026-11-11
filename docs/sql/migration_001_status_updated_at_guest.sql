-- [NEW FILE] Added in the Status / GUEST update.
-- Migration 001: add status + updated_at to roles and users, and add the GUEST role.
-- Safe to run on an existing database: existing rows are kept and every step
-- checks first, so running the script twice does nothing the second time.
-- Run it BEFORE starting the new version of the application.

USE user_management_db;
GO

-- ---------- roles ----------
IF COL_LENGTH('dbo.roles', 'status') IS NULL
    ALTER TABLE dbo.roles ADD
        status VARCHAR(10) NOT NULL
            CONSTRAINT df_roles_status DEFAULT 'ACTIVE'
            CONSTRAINT ck_roles_status CHECK (status IN ('ACTIVE', 'INACTIVE'));

IF COL_LENGTH('dbo.roles', 'updated_at') IS NULL
    ALTER TABLE dbo.roles ADD
        updated_at DATETIME2(6) NOT NULL
            CONSTRAINT df_roles_updated_at DEFAULT SYSDATETIME();
GO

-- ---------- users ----------
IF COL_LENGTH('dbo.users', 'status') IS NULL
    ALTER TABLE dbo.users ADD
        status VARCHAR(10) NOT NULL
            CONSTRAINT df_users_status DEFAULT 'ACTIVE'
            CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'INACTIVE'));

-- Existing users get their created_at as the first updated_at value
IF COL_LENGTH('dbo.users', 'updated_at') IS NULL
BEGIN
    ALTER TABLE dbo.users ADD
        updated_at DATETIME2(6) NOT NULL
            CONSTRAINT df_users_updated_at DEFAULT SYSDATETIME();
    EXEC('UPDATE dbo.users SET updated_at = created_at');
END
GO

-- ---------- GUEST role ----------
-- With the original 3 roles this gets id 4. The application finds GUEST by name, not id.
IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE name = 'GUEST')
    INSERT INTO dbo.roles (name) VALUES ('GUEST');
GO

SELECT id, name, status, updated_at FROM dbo.roles ORDER BY id;
GO
