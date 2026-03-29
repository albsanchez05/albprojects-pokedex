CREATE TABLE pokemon (
    id BIGSERIAL PRIMARY KEY,
    pokedex_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    type1 VARCHAR(50) NOT NULL,
    type2 VARCHAR(50),
    hp INTEGER NOT NULL,
    attack INTEGER NOT NULL,
    defense INTEGER NOT NULL,
    sp_attack INTEGER NOT NULL,
    sp_defense INTEGER NOT NULL,
    speed INTEGER NOT NULL
);