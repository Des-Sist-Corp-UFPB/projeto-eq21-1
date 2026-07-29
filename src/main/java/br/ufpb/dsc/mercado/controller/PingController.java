package br.ufpb.dsc.mercado.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PingController {

    @GetMapping(value = {"/ping", "/ping/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", "eq21");
        response.put("timestamp", Instant.now().toString());
        return response;
    }
}