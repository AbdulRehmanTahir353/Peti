package com.peti.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Redirect authenticated users to wardrobe, unauthenticated to login
        return authentication != null ? "redirect:/wardrobe" : "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Renders login.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Renders register.html
    }
}