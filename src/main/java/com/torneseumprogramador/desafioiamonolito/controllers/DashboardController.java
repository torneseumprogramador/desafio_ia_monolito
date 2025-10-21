package com.torneseumprogramador.desafioiamonolito.controllers;

import com.torneseumprogramador.desafioiamonolito.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controller do Dashboard
 * Responsável pelas rotas do dashboard com estatísticas e gráficos
 */
@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Verificar autenticação
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        try {
            // Obter estatísticas dos usuários
            Map<String, Object> stats = userService.getUserStatistics();

            // Obter dados para gráfico de usuários por mês (últimos 6 meses)
            List<Map<String, Object>> monthlyData = userService.getMonthlyUserRegistrations(6);

            model.addAttribute("stats", stats);
            model.addAttribute("monthly_data", monthlyData);
            model.addAttribute("title", "Dashboard");

            return "dashboard/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao carregar dashboard: " + e.getMessage());
            return "redirect:/";
        }
    }

    // API endpoints para dashboard
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<?> apiStats(HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Map<String, Object> stats = userService.getUserStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/monthly-data")
    @ResponseBody
    public ResponseEntity<?> apiMonthlyData(HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            List<Map<String, Object>> monthlyData = userService.getMonthlyUserRegistrations(6);
            return ResponseEntity.ok(monthlyData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

