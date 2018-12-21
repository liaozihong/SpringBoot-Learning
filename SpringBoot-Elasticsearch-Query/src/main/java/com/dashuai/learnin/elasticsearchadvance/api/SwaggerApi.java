package com.dashuai.learnin.elasticsearchadvance.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger controller
 * <p/>
 * Created in 2018.11.08
 * <p/>
 *
 * @author Liaodashuai
 */
@Controller
public class SwaggerApi {

    /**
     * Swagger ui string.
     *
     * @return the string
     */
    @GetMapping("/")
    public String swaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}