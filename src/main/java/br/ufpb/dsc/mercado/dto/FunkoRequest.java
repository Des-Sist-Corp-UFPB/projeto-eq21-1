package br.ufpb.dsc.mercado.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record FunkoRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 120) String franquia,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal preco
) {}
