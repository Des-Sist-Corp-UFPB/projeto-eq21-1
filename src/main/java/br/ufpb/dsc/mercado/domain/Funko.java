package br.ufpb.dsc.mercado.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "funko")
public class Funko {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String franquia;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public Funko() {}

    public Funko(String nome, String franquia, BigDecimal preco) {
        this.nome = nome;
        this.franquia = franquia;
        this.preco = preco;
    }

    @PrePersist
    void prePersist() {
        criadoEm = Instant.now();
        atualizadoEm = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        atualizadoEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFranquia() { return franquia; }
    public void setFranquia(String franquia) { this.franquia = franquia; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
