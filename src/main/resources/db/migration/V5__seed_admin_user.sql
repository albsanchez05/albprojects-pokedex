-- Default admin account for initial local testing.
-- Password here corresponds to BCrypt hash of a temporary password.
INSERT INTO app_user( username, email, password, role )
VALUES
(
    'admin',
    'admin@pokedex.local',
    '$2a$10$7QJ4kqVV2slF8h9sFQmBxeEs8h9Q0Lq4Qv5mBq8R2d7C9m5f5g7cS',
    'ADMIN'
);