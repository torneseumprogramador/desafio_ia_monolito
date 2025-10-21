package com.torneseumprogramador.desafioiamonolito.controllers;

import com.torneseumprogramador.desafioiamonolito.interceptor.GuestInterceptor;
import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de Autenticação
 * Responsável pelas rotas de login, logout e registro
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private GuestInterceptor guestInterceptor;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) throws Exception {
        // Verificar se já está logado (guest_only)
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }
        
        model.addAttribute("title", "Login");
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username_or_email") String usernameOrEmail,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Verificar se já está logado
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }

        usernameOrEmail = usernameOrEmail != null ? usernameOrEmail.trim() : "";
        password = password != null ? password : "";

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, preencha todos os campos.");
            redirectAttributes.addFlashAttribute("username_or_email", usernameOrEmail);
            return "redirect:/auth/login";
        }

        // Autenticar usuário
        Object[] result = userService.authenticate(usernameOrEmail, password);
        User user = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("username_or_email", usernameOrEmail);
            return "redirect:/auth/login";
        }

        // Login bem-sucedido - criar sessão
        session.setAttribute("user_id", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("name", user.getName());
        session.setMaxInactiveInterval(604800); // 7 dias

        redirectAttributes.addFlashAttribute("success", String.format("Bem-vindo(a), %s!", user.getName()));

        // Redirecionar para a página que o usuário tentou acessar ou para home
        String nextPage = (String) session.getAttribute("next");
        session.removeAttribute("next");
        if (nextPage != null && !nextPage.isEmpty()) {
            return "redirect:" + nextPage;
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session, Model model) {
        // Verificar se já está logado
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }
        
        model.addAttribute("title", "Registrar");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password_confirm") String passwordConfirm,
            @RequestParam(value = "phone", required = false) String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Verificar se já está logado
        if (session.getAttribute("user_id") != null) {
            return "redirect:/";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name != null ? name.trim() : "");
        data.put("email", email != null ? email.trim() : "");
        data.put("username", username != null ? username.trim() : "");
        data.put("password", password);
        data.put("phone", phone != null ? phone.trim() : "");
        data.put("isActive", true);

        // Validar confirmação de senha
        if (!password.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("error", "As senhas não coincidem.");
            redirectAttributes.addFlashAttribute("data", data);
            return "redirect:/auth/register";
        }

        // Validar tamanho da senha
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "A senha deve ter no mínimo 6 caracteres.");
            redirectAttributes.addFlashAttribute("data", data);
            return "redirect:/auth/register";
        }

        // Criar usuário
        Object[] result = userService.createUser(data);
        User user = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("data", data);
            return "redirect:/auth/register";
        }

        // Registro bem-sucedido - fazer login automático
        session.setAttribute("user_id", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("name", user.getName());
        session.setMaxInactiveInterval(604800);

        redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso! Bem-vindo(a)!");
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String userName = (String) session.getAttribute("name");
        userName = userName != null ? userName : "Usuário";

        // Limpar sessão
        session.invalidate();

        redirectAttributes.addFlashAttribute("info", String.format("Até logo, %s!", userName));
        return "redirect:/";
    }
}

