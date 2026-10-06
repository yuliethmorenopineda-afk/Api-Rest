package com.mundial.app.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Solo renderiza la vista de la sección. Los datos llegan desde la API REST. */
@Controller
public class EstadioWebControlador {

    // @GetMapping: responde a peticiones GET de esa ruta. El texto devuelto es el nombre
    // del archivo en src/main/resources/templates (sin .html).
    @GetMapping("/estadios")
    public String vista() {
        return "estadio";
    }
}
