-- Remove legacy integration-test users that were created in the main PostgreSQL database.
-- These accounts follow the test pattern user-<random>.
DELETE FROM app_user
WHERE username LIKE 'user-%';

-- Keep the sequence aligned after cleanup.
SELECT setval( 'app_user_id_seq', COALESCE( ( SELECT MAX( id ) FROM app_user ), 1 ), true );

