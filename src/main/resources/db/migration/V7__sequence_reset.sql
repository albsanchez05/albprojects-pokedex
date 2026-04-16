 SELECT setval('app_user_id_seq', COALESCE(( SELECT MAX(id) FROM app_user ), 1), true);
