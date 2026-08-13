package com.veckos.VECKOS_Backend.runners;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Intencionalmente vacio.
        // El proyecto ya no inicializa datos mockeados ni credenciales por defecto al arrancar.
    }
}
