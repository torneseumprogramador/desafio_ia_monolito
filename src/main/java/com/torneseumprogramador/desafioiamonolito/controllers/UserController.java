package com.torneseumprogramador.desafioiamonolito.controllers;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller de Usuários
 * Responsável pelas rotas e requisições HTTP relacionadas a usuários
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String index(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "per_page", defaultValue = "10") int perPage,
            HttpSession session,
            Model model) {
        
        // Verificar autenticação
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Object[] result = userService.getAllUsers(page, perPage);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) result[0];
        long total = (long) result[1];

        // Calcular informações de paginação
        int totalPages = (int) Math.ceil((double) total / perPage);

        model.addAttribute("users", users);
        model.addAttribute("page", page);
        model.addAttribute("per_page", perPage);
        model.addAttribute("total", total);
        model.addAttribute("total_pages", totalPages);
        model.addAttribute("title", "Usuários");

        return "users/index";
    }

    @GetMapping("/create")
    public String createPage(HttpSession session, Model model) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Criar Usuário");
        return "users/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "is_active", required = false) boolean isActive,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("username", username);
        data.put("password", password);
        data.put("phone", phone);
        data.put("isActive", isActive);

        Object[] result = userService.createUser(data);
        User user = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("data", data);
            return "redirect:/users/create";
        }

        redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso!");
        return "redirect:/users";
    }

    @GetMapping("/{userId}")
    public String show(
            @PathVariable("userId") Long userId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/users";
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("title", "Detalhes do Usuário");
        return "users/show";
    }

    @GetMapping("/{userId}/edit")
    public String editPage(
            @PathVariable("userId") Long userId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/users";
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("title", "Editar Usuário");
        return "users/edit";
    }

    @PostMapping("/{userId}/edit")
    public String edit(
            @PathVariable("userId") Long userId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "is_active", required = false) boolean isActive,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Optional<User> optionalUser = userService.getUserById(userId);
        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/users";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("username", username);
        data.put("phone", phone);
        data.put("isActive", isActive);

        // Só atualiza a senha se foi informada
        if (password != null && !password.trim().isEmpty()) {
            data.put("password", password);
        }

        Object[] result = userService.updateUser(userId, data);
        User updatedUser = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/users/" + userId + "/edit";
        }

        redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
        return "redirect:/users/" + userId;
    }

    @PostMapping("/{userId}/delete")
    public String delete(
            @PathVariable("userId") Long userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Object[] result = userService.deleteUser(userId);
        boolean success = (boolean) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Usuário removido com sucesso!");
        }

        return "redirect:/users";
    }

    @PostMapping("/{userId}/toggle-status")
    public String toggleStatus(
            @PathVariable("userId") Long userId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Object[] result = userService.toggleUserStatus(userId);
        User user = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            String status = user.getIsActive() ? "ativado" : "desativado";
            redirectAttributes.addFlashAttribute("success", String.format("Usuário %s com sucesso!", status));
        }

        return "redirect:/users";
    }

    // API Endpoints
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiList(HttpSession session) {
        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).build();
        }

        Object[] result = userService.getAllUsers(1, 100);
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) result[0];
        long total = (long) result[1];

        Map<String, Object> response = new HashMap<>();
        response.put("users", users.stream().map(User::toDict).toArray());
        response.put("total", total);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{userId}")
    @ResponseBody
    public ResponseEntity<?> apiGet(
            @PathVariable("userId") Long userId,
            HttpSession session) {
        
        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuário não encontrado");
            return ResponseEntity.status(404).body(error);
        }

        return ResponseEntity.ok(optionalUser.get().toDict());
    }
}

