package br.ufpb.dsc.mercado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // Encaminha rotas do React Router (sem extensão de arquivo) para o index.html.
    // Exclui /assets/** para que o handler de recursos estáticos sirva JS/CSS/imagens.
    @GetMapping(value = {"/{path:^(?!assets)[^\\.]+}", "/{path:^(?!assets)[^\\.]+}/**"})
    public String spa() {
        return "forward:/index.html";
    }
}
