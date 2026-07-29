package br.ufpb.dsc.mercado.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PingController {
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", "eq21");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}