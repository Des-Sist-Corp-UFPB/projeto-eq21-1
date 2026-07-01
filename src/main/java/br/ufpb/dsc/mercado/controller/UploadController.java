package br.ufpb.dsc.mercado.controller;

import br.ufpb.dsc.mercado.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(value = "/imagens", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadImagens(
            @RequestParam("imagens") List<MultipartFile> imagens) throws IOException {
        return ResponseEntity.ok(uploadService.uploadImagens(imagens));
    }
}
