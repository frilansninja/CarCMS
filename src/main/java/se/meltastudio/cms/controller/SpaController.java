package se.meltastudio.cms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle React SPA routing.
 * Forwards all non-API requests to index.html for client-side routing.
 */
@Controller
public class SpaController {

    /**
     * Forward all non-API routes to index.html to support React Router.
     * This allows users to refresh the page on any React route.
     *
     * Note: Spring Security and API controllers are mapped first,
     * so this catch-all won't interfere with /api/** endpoints.
     */
    @RequestMapping(value = {
        "/",
        "/login",
        "/dashboard",
        "/users/**",
        "/customers/**",
        "/vehicles/**",
        "/workorders/**",
        "/calendar/**",
        "/invoices/**",
        "/articles/**",
        "/suppliers/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
