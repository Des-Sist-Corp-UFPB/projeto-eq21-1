CREATE TABLE funko (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    franquia      VARCHAR(120) NOT NULL,
    preco         NUMERIC(10, 2) NOT NULL CHECK (preco >= 0),
    imagem_url    TEXT,
    criado_em     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_funko_nome ON funko (nome);
CREATE INDEX idx_funko_franquia ON funko (franquia);
