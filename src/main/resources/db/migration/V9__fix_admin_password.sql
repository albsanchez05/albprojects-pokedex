-- Correct admin password hash (BCrypt hash of Admin@123)
UPDATE app_user
SET password = '$2a$10$jT.VHaRB9Sdm4928Gb5Q8.zRErpiRUtZ1xxZ9L5bYYNapnUwqX/WG'
WHERE username = 'admin';

