package br.ufpb.dsc.mercado.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PingController — Testes Unitários")
class PingControllerTest {

    private final PingController controller = new PingController();

    @Test
    @DisplayName("ping: deve retornar status 'ok', service 'eq21' e timestamp não-nulo")
    void ping_deveRetornarCamposCorretos() {
        Map<String, Object> resultado = controller.ping();

        assertThat(resultado.get("status")).isEqualTo("ok");
        assertThat(resultado.get("service")).isEqualTo("eq21");
        assertThat(resultado.get("timestamp")).isNotNull().isInstanceOf(String.class);
    }
}
