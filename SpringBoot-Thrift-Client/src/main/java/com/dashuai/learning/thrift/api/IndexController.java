package com.dashuai.learning.thrift.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Index controller
 * <p/>
 * Created in 2019.07.01
 * <p/>
 *
 * @author Liaozihong
 */
@Controller
public class IndexController {
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
