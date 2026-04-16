CREATE TABLE app_user
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR( 50 ) NOT NULL,
    email VARCHAR( 100 ) NOT NULL,
    password VARCHAR( 255 ) NOT NULL,
    role VARCHAR( 20 ) NOT NULL,
    CONSTRAINT uk_app_user_username UNIQUE( username ),
    CONSTRAINT uk_app_user_email UNIQUE( email ),
    CONSTRAINT chk_app_user_role CHECK( role IN ( 'USER', 'ADMIN' ) )
);

