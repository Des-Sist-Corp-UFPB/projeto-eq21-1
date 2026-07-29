package br.ufpb.dsc.mercado.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.application.name:eq21}")
    private String serviceName;

    public PingController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = {"/ping", "/ping/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping(@RequestParam(value = "service", required = false) String serviceOverride) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "ok");
        response.put("service", serviceOverride != null ? serviceOverride : serviceName);
        response.put("timestamp", Instant.now().toString());

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        } catch (Exception e) {
            log.warn("Falha de verificação de conexão com banco no /ping", e);
            response.put("status", "degraded");
            response.put("database", "down");
            response.put("databaseError", e.getMessage());
        }

        return response;
    }
}