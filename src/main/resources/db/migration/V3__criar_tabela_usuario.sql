CREATE TABLE usuario (
    id    BIGSERIAL    PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role  VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER'
);

CREATE INDEX idx_usuario_email ON usuario (email);
