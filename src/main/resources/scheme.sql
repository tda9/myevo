-- -- Drop the database if it exists (optional, be careful)
--  --DROP DATABASE IF EXISTS iam_test;
-- -- Create the `users` table
-- CREATE TABLE users
-- (
--     user_id  SERIAL PRIMARY KEY,           -- Auto-incrementing unique identifier
--     email    VARCHAR(255) NOT NULL UNIQUE, -- Unique email
--     password VARCHAR(255) NOT NULL,        -- Password (hashing recommended)
--     phone    VARCHAR(15),                  -- Optional phone number
--     dob      DATE,                         -- Date of birth
--     image    VARCHAR(255)                  -- Path or URL to the user's image
-- );
--
-- -- Create the `roles` table
-- CREATE TABLE roles
-- (
--     role_id SERIAL PRIMARY KEY,         -- Auto-incrementing unique identifier
--     name    VARCHAR(50) NOT NULL UNIQUE -- Role name, e.g., 'ADMIN', 'USER'
-- );
--
-- -- Create the `user_roles` table (many-to-many relationship between users and roles)
-- CREATE TABLE user_roles
-- (
--     user_id     INT NOT NULL,                        -- Foreign key to `users`
--     role_id     INT NOT NULL,                        -- Foreign key to `roles`
--     assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp when the role was assigned
--     PRIMARY KEY (user_id, role_id)                   -- Composite primary key
-- );
--
-- -- Add foreign key constraints using ALTER TABLE
-- ALTER TABLE user_roles
--     ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;
--
-- ALTER TABLE user_roles
--     ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE;
--
-- -- Create the `password_reset_token` table
-- CREATE TABLE password_reset_token
-- (
--     token_id        SERIAL PRIMARY KEY,                  -- Auto-incrementing unique identifier for each token
--     token           VARCHAR(255) NOT NULL,               -- The unique password reset token
--     expiration_date TIMESTAMP    NOT NULL,               -- When the token expires
--     created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for token creation
--     user_id         INT          NOT NULL                -- Foreign key column, constraint added later
-- );
--
-- -- Add the foreign key constraint for `password_reset_token` separately
-- ALTER TABLE password_reset_token
--     ADD CONSTRAINT fk_user_password_reset FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;
--
-- -- Drop the foreign key constraints first
-- ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_user;
-- ALTER TABLE user_roles DROP CONSTRAINT IF EXISTS fk_role;
-- ALTER TABLE password_reset_token DROP CONSTRAINT IF EXISTS fk_user_password_reset;
-- INSERT INTO public.roles(name)
-- VALUES ('USER');
-- INSERT INTO public.roles(name)
-- VALUES ('ADMIN');
-- -- Drop the tables
-- -- DROP TABLE IF EXISTS password_reset_token;
-- -- DROP TABLE IF EXISTS user_roles;
-- -- DROP TABLE IF EXISTS roles;
-- -- DROP TABLE IF EXISTS users;

-- Drop the tables in reverse order of dependencies
DROP TABLE IF EXISTS password_reset_token CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create the `users` table
CREATE TABLE users
(
    user_id    SERIAL PRIMARY KEY,           -- Auto-incrementing unique identifier
    email      VARCHAR(255) NOT NULL UNIQUE, -- Unique email
    password   VARCHAR(255) NOT NULL,        -- Password (hashing recommended)
    phone      VARCHAR(15),                  -- Optional phone number
    dob        DATE,                         -- Date of birth
    image      VARCHAR(255),
    is_confirm BOOLEAN-- Path or URL to the user's image
);

-- Create the `roles` table
CREATE TABLE roles
(
    role_id SERIAL PRIMARY KEY,         -- Auto-incrementing unique identifier
    name    VARCHAR(50) NOT NULL UNIQUE -- Role name, e.g., 'ADMIN', 'USER'
);

-- Create the `user_roles` table (many-to-many relationship between users and roles)
CREATE TABLE user_roles
(
    user_id     INT NOT NULL,                        -- Foreign key to `users`
    role_id     INT NOT NULL,                        -- Foreign key to `roles`
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp when the role was assigned
    PRIMARY KEY (user_id, role_id),                  -- Composite primary key
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE
);

-- Create the `password_reset_token` table
CREATE TABLE password_reset_token
(
    token_id        SERIAL PRIMARY KEY,                  -- Auto-incrementing unique identifier for each token
    token           VARCHAR(255) NOT NULL,               -- The unique password reset token
    expiration_date TIMESTAMP    NOT NULL,               -- When the token expires
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for token creation
    user_id         INT          NOT NULL,               -- Foreign key to `users`
    CONSTRAINT fk_user_password_reset FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO roles (name)
VALUES ('USER'),
       ('ADMIN');
-- Drop the table if it exists
DROP TABLE IF EXISTS black_list_token CASCADE;

-- Create the `black_list_token` table
CREATE TABLE black_list_token
(
    token_id        SERIAL PRIMARY KEY,                  -- Auto-incrementing unique identifier
    token           VARCHAR(255) NOT NULL,               -- The token string itself
    expiration_date TIMESTAMP    NOT NULL,               -- Expiration date of the token
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for when the token was created
    user_id         INT          NOT NULL,               -- Foreign key to `users`
    CONSTRAINT fk_user_blacklist FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

-- Drop the table if it exists
DROP TABLE IF EXISTS user_audit CASCADE;

-- Create the `user_audit` table
CREATE TABLE user_audit
(
    audit_id       SERIAL PRIMARY KEY,                  -- Auto-incrementing unique identifier for the audit
    user_agent     VARCHAR(255),                        -- User agent information from the request
    ip             VARCHAR(50),                         -- IP address from which the request was made
    request_method VARCHAR(10),                         -- HTTP request method (GET, POST, etc.)
    url            VARCHAR(255),                        -- The URL that was requested
    user_id        INT NOT NULL,                        -- Foreign key to `users`
    email          VARCHAR(255),                        -- User email
    password       VARCHAR(255),                        -- User password (hashed)
    phone          VARCHAR(15),                         -- User phone number
    dob            DATE,                                -- Date of birth
    image          VARCHAR(255),                        -- Image (URL or path)
    change_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for when the change occurred
    CONSTRAINT fk_user_audit FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);