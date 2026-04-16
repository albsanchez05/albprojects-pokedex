CREATE TABLE pokemon
(
    id BIGSERIAL PRIMARY KEY,
    pokedex_id INT NOT NULL UNIQUE,
    name VARCHAR( 100 ) NOT NULL UNIQUE,
    type1 VARCHAR( 20 ) NOT NULL,
    type2 VARCHAR( 20 ),
    hp INT NOT NULL,
    attack INT NOT NULL,
    defense INT NOT NULL,
    sp_attack INT NOT NULL,
    sp_defense INT NOT NULL,
    speed INT NOT NULL,
    image VARCHAR( 255 ),
    captured BOOLEAN DEFAULT FALSE
);

