package com.vitalink.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige todas las rutas del SPA (Angular) a index.html.
 * Necesario para que /perfil, /admin, /public/:id funcionen correctamente.
 */
@Controller
public class SpaController {

    @GetMapping(value = { "/", "/{path:[^\\.]*}", "/{path:^(?!api).*$}/**/{path:[^\\.]*}" })
    public String spa() {
        return "forward:/index.html";
    }
}
