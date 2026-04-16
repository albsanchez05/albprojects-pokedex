-- Seed test admin user (password: TestAdmin@123)
INSERT INTO app_user( username, email, password, role )
VALUES
(
    'admin',
    'admin@pokedex.local',
    '$2a$10$jT.VHaRB9Sdm4928Gb5Q8.zRErpiRUtZ1xxZ9L5bYYNapnUwqX/WG',
    'ADMIN'
);

