package br.ufpb.dsc.mercado.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // Encaminha rotas do React Router (sem extensão de arquivo) para o index.html.
    // Padrão [^\\.] exclui requisições de arquivos (.js, .css, .png, etc.),
    // que são servidos diretamente pelo handler de recursos estáticos.
    @GetMapping(value = {"/{path:[^\\.]*}", "/{path:[^\\.]*}/**"})
    public String spa() {
        return "forward:/index.html";
    }
}
