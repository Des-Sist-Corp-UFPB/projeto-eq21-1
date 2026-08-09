package br.ufpb.dsc.mercado.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "log_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String recurso;

    @Column(name = "recurso_id", length = 50)
    private String recursoId;

    @Column(nullable = false, length = 50)
    private String operacao;

    @Column(nullable = false, length = 255)
    private String usuario;

    @Column(length = 500)
    private String detalhe;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(name = "mensagem_erro", length = 500)
    private String mensagemErro;

    @Column(name = "ocorrido_em", nullable = false, updatable = false)
    private Instant ocorridoEm;

    public LogAuditoria() {}

    public LogAuditoria(String recurso, String recursoId, String operacao,
                        String usuario, String detalhe, String status, String mensagemErro) {
        this.recurso = recurso;
        this.recursoId = recursoId;
        this.operacao = operacao;
        this.usuario = usuario;
        this.detalhe = detalhe;
        this.status = status;
        this.mensagemErro = mensagemErro;
    }

    @PrePersist
    void prePersist() {
        ocorridoEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getRecurso() { return recurso; }
    public String getRecursoId() { return recursoId; }
    public String getOperacao() { return operacao; }
    public String getUsuario() { return usuario; }
    public String getDetalhe() { return detalhe; }
    public String getStatus() { return status; }
    public String getMensagemErro() { return mensagemErro; }
    public Instant getOcorridoEm() { return ocorridoEm; }
}
