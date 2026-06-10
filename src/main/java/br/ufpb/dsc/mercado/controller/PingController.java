package br.ufpb.dsc.mercado.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class PingController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "service", "eq21",
                "timestamp", Instant.now().toString()
        ));
    }
}
