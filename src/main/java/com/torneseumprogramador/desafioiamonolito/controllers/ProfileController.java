package com.torneseumprogramador.desafioiamonolito.controllers;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller de Perfil do Usuário
 * Responsável pelas rotas de gerenciamento do perfil do usuário logado
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    public String profile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/dashboard";
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("title", "Meu Perfil");
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editProfilePage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/dashboard";
        }

        model.addAttribute("user", optionalUser.get());
        model.addAttribute("title", "Editar Perfil");
        return "profile/edit_profile";
    }

    @PostMapping("/edit")
    public String editProfile(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam(value = "phone", required = false) String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (session.getAttribute("user_id") == null) {
            return "redirect:/auth/login";
        }

        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado");
            return "redirect:/dashboard";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", name != null ? name.trim() : "");
        data.put("email", email != null ? email.trim() : "");
        data.put("username", username != null ? username.trim() : "");
        data.put("phone", phone != null ? phone.trim() : "");

        // Atualizar dados
        Object[] result = userService.updateUser(userId, data);
        User updatedUser = (User) result[0];
        String error = (String) result[1];

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            redirectAttributes.addFlashAttribute("data", data);
            return "redirect:/profile/edit";
        }

        // Atualizar dados da sessão
        session.setAttribute("name", updatedUser.getName());
        session.setAttribute("username", updatedUser.getUsername());

        redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestParam("current_password") String currentPassword,
            @RequestParam("new_password") String newPassword,
            @RequestParam("confirm_password") String confirmPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();

        if (session.getAttribute("user_id") == null) {
            response.put("success", false);
            response.put("message", "Usuário não autenticado");
            return ResponseEntity.status(401).body(response);
        }

        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> optionalUser = userService.getUserById(userId);

        if (!optionalUser.isPresent()) {
            response.put("success", false);
            response.put("message", "Usuário não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        User user = optionalUser.get();

        // Validar senha atual
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            response.put("success", false);
            response.put("message", "Senha atual incorreta");
            return ResponseEntity.status(400).body(response);
        }

        // Validar nova senha
        if (newPassword.length() < 6) {
            response.put("success", false);
            response.put("message", "Nova senha deve ter no mínimo 6 caracteres");
            return ResponseEntity.status(400).body(response);
        }

        if (!newPassword.equals(confirmPassword)) {
            response.put("success", false);
            response.put("message", "Confirmação de senha não confere");
            return ResponseEntity.status(400).body(response);
        }

        // Atualizar senha
        Map<String, Object> data = new HashMap<>();
        data.put("password", newPassword);
        Object[] result = userService.updateUser(userId, data);
        String error = (String) result[1];

        if (error != null) {
            response.put("success", false);
            response.put("message", error);
            return ResponseEntity.status(400).body(response);
        }

        response.put("success", true);
        response.put("message", "Senha alterada com sucesso!");
        return ResponseEntity.ok(response);
    }
}

