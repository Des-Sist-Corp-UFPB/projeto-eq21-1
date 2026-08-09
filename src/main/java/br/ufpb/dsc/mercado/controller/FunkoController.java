package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.dto.FunkoAnaliseResponse;
import br.ufpb.dsc.mercado.dto.FunkoRequest;
import br.ufpb.dsc.mercado.dto.FunkoResponse;
import br.ufpb.dsc.mercado.service.FunkoAnaliseService;
import br.ufpb.dsc.mercado.service.FunkoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/funkos")
public class FunkoController {

    private final FunkoService service;
    private final FunkoAnaliseService analiseService;

    public FunkoController(FunkoService service, FunkoAnaliseService analiseService) {
        this.service = service;
        this.analiseService = analiseService;
    }

    @GetMapping
    public Page<FunkoResponse> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20) Pageable pageable) {

        if (busca != null && !busca.isBlank()) {
            return service.buscar(busca, pageable).map(FunkoResponse::from);
        }
        return service.listar(pageable).map(FunkoResponse::from);
    }

    @GetMapping("/{id}")
    public FunkoResponse buscarPorId(@PathVariable Long id) {
        return FunkoResponse.from(service.buscarPorId(id));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<FunkoResponse> criar(
            @Valid @ModelAttribute FunkoRequest request,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem)
            throws IOException {

        FunkoResponse response = FunkoResponse.from(service.criar(request, imagem));
        return ResponseEntity
                .created(URI.create("/api/funkos/" + response.id()))
                .body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public FunkoResponse atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute FunkoRequest request,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem)
            throws IOException {

        return FunkoResponse.from(service.atualizar(id, request, imagem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/analyze-image", consumes = "multipart/form-data")
    public ResponseEntity<FunkoAnaliseResponse> analisarImagem(
            @RequestPart("imagem") MultipartFile imagem) throws IOException {
        return ResponseEntity.ok(analiseService.analisarImagem(imagem));
    }
}
