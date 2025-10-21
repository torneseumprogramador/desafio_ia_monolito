package com.torneseumprogramador.desafioiamonolito.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller para rotas principais
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // Se usuário estiver logado, redirecionar para dashboard
        if (session.getAttribute("user_id") != null) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("title", "Home");
        return "main/index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Sobre");
        return "main/about";
    }
}
