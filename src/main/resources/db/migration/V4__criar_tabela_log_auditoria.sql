CREATE TABLE log_auditoria (
    id          BIGSERIAL PRIMARY KEY,
    recurso     VARCHAR(50)  NOT NULL,
    recurso_id  VARCHAR(50),
    operacao    VARCHAR(50)  NOT NULL,
    usuario     VARCHAR(255) NOT NULL,
    detalhe     VARCHAR(500),
    status      VARCHAR(10)  NOT NULL,
    mensagem_erro VARCHAR(500),
    ocorrido_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_log_auditoria_recurso     ON log_auditoria (recurso);
CREATE INDEX idx_log_auditoria_usuario     ON log_auditoria (usuario);
CREATE INDEX idx_log_auditoria_ocorrido_em ON log_auditoria (ocorrido_em);
