package com.mundial.app.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Entrega la página de inicio. @Controller devuelve el NOMBRE de una vista Thymeleaf
 * (home -> templates/home.html), a diferencia de @RestController que devuelve JSON.
 */
@Controller
public class HomeControlador {

    // @GetMapping: responde a peticiones GET de esa ruta. El texto devuelto es el nombre
    // del archivo en src/main/resources/templates (sin .html).
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
