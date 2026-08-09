package br.ufpb.dsc.mercado.dto;

import java.math.BigDecimal;

public record FunkoAnaliseResponse(String nome, String franquia, BigDecimal preco) {}
