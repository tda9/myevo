-- Create the `users` table
CREATE TABLE users
(
    user_id  SERIAL PRIMARY KEY,           -- Auto-incrementing unique identifier
    email    VARCHAR(255) NOT NULL UNIQUE, -- Unique email
    password VARCHAR(255) NOT NULL,        -- Password (hashing recommended)
    phone    VARCHAR(15),                  -- Optional phone number
    dob      DATE,                         -- Date of birth
    image    VARCHAR(255)                  -- Path or URL to the user's image
--                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for creation
--                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp for updates
);

-- Create the `roles` table
CREATE TABLE roles
(
    role_id SERIAL PRIMARY KEY,         -- Auto-incrementing unique identifier
    name    VARCHAR(50) NOT NULL UNIQUE -- Role name, e.g., 'ADMIN', 'USER'
);

-- Create the `role_user` table
CREATE TABLE role_user
(
    user_id     INT NOT NULL,                        -- Foreign key to `users`
    role_id     INT NOT NULL,                        -- Foreign key to `roles`
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp when the role was assigned
    PRIMARY KEY (user_id, role_id)                   -- Composite primary key
);

-- Add constraints using ALTER CONSTRAINT
ALTER TABLE role_user
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

ALTER TABLE role_user
    ADD CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE;

-- Create the `password_reset_token` table
CREATE TABLE password_reset_token
(
    token_id        SERIAL PRIMARY KEY,                  -- Auto-incrementing unique identifier for each token
    user_id         INT          NOT NULL,               -- Foreign key linking to the users table
    token           VARCHAR(255) NOT NULL UNIQUE,        -- The unique password reset token
    expiration_date TIMESTAMP    NOT NULL,               -- When the token expires
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp for token creation
    CONSTRAINT fk_user_password_reset FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);
