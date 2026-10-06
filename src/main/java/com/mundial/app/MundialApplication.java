package com.mundial.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal. Aquí arranca todo.
 *
 * @SpringBootApplication junta tres cosas: configuración automática, escaneo de
 * componentes (encuentra los @Controller, @RestController, repositorios...) y
 * configuración de Spring. SpringApplication.run levanta el servidor web (Tomcat)
 * en el puerto indicado en application.properties.
 */
@SpringBootApplication
public class MundialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MundialApplication.class, args);
    }
}
