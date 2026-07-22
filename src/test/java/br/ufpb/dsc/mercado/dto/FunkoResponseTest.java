package br.ufpb.dsc.mercado.dto;

import br.ufpb.dsc.mercado.domain.Funko;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FunkoResponse — Testes Unitários")
class FunkoResponseTest {

    @Test
    @DisplayName("from: deve mapear nome, franquia e preco do Funko")
    void from_deveMappearCamposBasicos() {
        Funko funko = new Funko("Batman", "DC Comics", new BigDecimal("89.90"));

        FunkoResponse response = FunkoResponse.from(funko);

        assertThat(response.nome()).isEqualTo("Batman");
        assertThat(response.franquia()).isEqualTo("DC Comics");
        assertThat(response.preco()).isEqualByComparingTo(new BigDecimal("89.90"));
        assertThat(response.imagemUrl()).isNull();
        assertThat(response.id()).isNull();
    }

    @Test
    @DisplayName("from: deve mapear imagemUrl quando presente")
    void from_deveMappearImagemUrl() {
        Funko funko = new Funko("Naruto", "Funko Pop", new BigDecimal("49.90"));
        funko.setImagemUrl("http://localhost:9000/bucket/funko.png");

        FunkoResponse response = FunkoResponse.from(funko);

        assertThat(response.imagemUrl()).isEqualTo("http://localhost:9000/bucket/funko.png");
    }

    @Test
    @DisplayName("record: campos devem ser acessíveis via accessors")
    void record_acessoresDevemFuncionar() {
        FunkoResponse response = new FunkoResponse(1L, "Goku", "Outro",
                new BigDecimal("55.00"), null, null, null);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Goku");
        assertThat(response.franquia()).isEqualTo("Outro");
        assertThat(response.preco()).isEqualByComparingTo(new BigDecimal("55.00"));
    }
}
