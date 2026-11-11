-- [NEW FILE - password] Added in the Password update.
-- Migration 002: add the password_hash column to users.
-- It stores the BCrypt HASH of the password (about 60 characters), never the real password.
-- It allows NULL: existing users have no password until someone edits them and sets one.
-- Safe to run more than once. Run it BEFORE starting the new version of the application.

USE user_management_db;
GO

IF COL_LENGTH('dbo.users', 'password_hash') IS NULL
    ALTER TABLE dbo.users ADD password_hash VARCHAR(100) NULL;
GO

SELECT id, name, email,
       CASE WHEN password_hash IS NULL THEN 'no' ELSE 'yes' END AS has_password
FROM dbo.users
ORDER BY id;
GO
