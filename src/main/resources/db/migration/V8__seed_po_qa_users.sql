-- Seed dedicated PO/QA users for manual RBAC verification in non-production environments.
-- Passwords are BCrypt hashes; temporary plain passwords must be shared out-of-band and rotated.
INSERT INTO app_user( username, email, password, role )
VALUES
(
    'po-admin',
    'po-admin@pokedex.local',
    '$2a$10$NAy.ccPsHXjYmWCHadqogeUuTwFJsjMiQK3JWWJN2EaU.Za/5.eZm',
    'ADMIN'
),
(
    'qa-user',
    'qa-user@pokedex.local',
    '$2a$10$BryG4jEDQ9Isotg58HLqyOFc0A4jOKUnggov17hTvHQ9wKTj91sda',
    'USER'
)
ON CONFLICT DO NOTHING;

