package com.example.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller to handle Swagger UI redirects
 */
@Controller
public class SwaggerController {

    /**
     * Redirect from /swagger-ui to /swagger-ui.html
     */
    @GetMapping("/swagger-ui")
    public String swaggerUi() {
        return "redirect:/swagger-ui.html";
    }
    
    /**
     * Redirect from /api/swagger-ui to /api/swagger-ui.html
     */
    @GetMapping("/api/swagger-ui")
    public String apiSwaggerUi() {
        return "redirect:/api/swagger-ui.html";
    }
    
    /**
     * Redirect root API to Swagger UI
     */
    @GetMapping("/api")
    public String apiRoot() {
        return "redirect:/api/swagger-ui.html";
    }
}
