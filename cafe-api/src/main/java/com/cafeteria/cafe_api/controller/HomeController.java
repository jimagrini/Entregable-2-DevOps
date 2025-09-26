package com.cafeteria.cafe_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "☕ Bienvenido a la API de la cafetería!";
    }

    @GetMapping("/health")
    public String health() {
        return "✅ API OK - funcionando correctamente";
    }
}
