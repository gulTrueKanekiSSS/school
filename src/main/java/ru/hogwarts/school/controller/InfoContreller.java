package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class InfoController {

    // Сюда Spring инжектит значение из application.properties
    @Value("${server.port}")
    private String port;

    @GetMapping("/port")
    public String getPort() {
        // просто возвращаем строкой значение порта
        return port;
    }
}
