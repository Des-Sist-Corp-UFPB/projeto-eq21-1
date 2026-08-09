package br.ufpb.dsc.mercado.dto;

import br.ufpb.dsc.mercado.domain.Funko;

import java.math.BigDecimal;
import java.time.Instant;

public record FunkoResponse(
        Long id,
        String nome,
        String franquia,
        BigDecimal preco,
        String imagemUrl,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static FunkoResponse from(Funko funko) {
        return new FunkoResponse(
                funko.getId(),
                funko.getNome(),
                funko.getFranquia(),
                funko.getPreco(),
                funko.getImagemUrl(),
                funko.getCriadoEm(),
                funko.getAtualizadoEm()
        );
    }
}
