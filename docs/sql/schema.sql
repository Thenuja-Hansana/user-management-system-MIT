USE user_management_db;
GO

IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL DROP TABLE dbo.roles;
GO

CREATE TABLE dbo.roles (
    id         BIGINT IDENTITY(1,1) NOT NULL,
    name       VARCHAR(50) NOT NULL,
    status     VARCHAR(10) NOT NULL CONSTRAINT df_roles_status DEFAULT 'ACTIVE',                -- [ADDED]
    updated_at DATETIME2(6) NOT NULL CONSTRAINT df_roles_updated_at DEFAULT SYSDATETIME(),      -- [ADDED]
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name),
    CONSTRAINT ck_roles_status CHECK (status IN ('ACTIVE', 'INACTIVE'))                         -- [ADDED]
);

CREATE TABLE dbo.users (
    id         BIGINT IDENTITY(1,1) NOT NULL,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NULL,                                                            -- [ADDED - password] BCrypt hash, NULL = no password yet
    created_at DATETIME2 NOT NULL CONSTRAINT df_users_created_at DEFAULT SYSDATETIME(),
    updated_at DATETIME2(6) NOT NULL CONSTRAINT df_users_updated_at DEFAULT SYSDATETIME(),      -- [ADDED]
    status     VARCHAR(10) NOT NULL CONSTRAINT df_users_status DEFAULT 'ACTIVE',                -- [ADDED]
    role_id    BIGINT NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES dbo.roles (id),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'INACTIVE'))                         -- [ADDED]
);
GO
