package com.torneseumprogramador.desafioiamonolito.integration;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de cenários de erro e validação
 */
@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpSession session;
    private User admin;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setIsActive(true);
        admin = userRepository.save(admin);

        session = new MockHttpSession();
        session.setAttribute("user_id", admin.getId());
        session.setAttribute("username", admin.getUsername());
        session.setAttribute("name", admin.getName());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        // Tentar criar sem dados obrigatórios
        mockMvc.perform(post("/users/create").session(session)
                        .param("name", "")
                        .param("email", "")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/create*"));
    }

    @Test
    void testEditUserNotFound() throws Exception {
        mockMvc.perform(get("/users/999/edit").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users*"));
    }

    @Test
    void testEditUserPostNotFound() throws Exception {
        mockMvc.perform(post("/users/999/edit").session(session)
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("username", "test")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users*"));
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        mockMvc.perform(post("/users/999/delete").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testToggleStatusNotFound() throws Exception {
        mockMvc.perform(post("/users/999/toggle-status").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProfileNotFound() throws Exception {
        MockHttpSession invalidSession = new MockHttpSession();
        invalidSession.setAttribute("user_id", 999L);

        mockMvc.perform(get("/profile").session(invalidSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/dashboard*"));
    }

    @Test
    void testEditProfileNotFound() throws Exception {
        MockHttpSession invalidSession = new MockHttpSession();
        invalidSession.setAttribute("user_id", 999L);

        mockMvc.perform(post("/profile/edit").session(invalidSession)
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("username", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/dashboard*"));
    }

    @Test
    void testChangePasswordUserNotFound() throws Exception {
        MockHttpSession invalidSession = new MockHttpSession();
        invalidSession.setAttribute("user_id", 999L);

        mockMvc.perform(post("/profile/change-password").session(invalidSession)
                        .param("current_password", "old")
                        .param("new_password", "new123456")
                        .param("confirm_password", "new123456"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUpdateProfileWithDuplicateEmail() throws Exception {
        // Criar outro usuário
        User otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@test.com");
        otherUser.setUsername("otheruser");
        otherUser.setPasswordHash(passwordEncoder.encode("password123"));
        otherUser.setIsActive(true);
        otherUser = userRepository.save(otherUser);

        // Tentar atualizar admin com email do outro usuário
        mockMvc.perform(post("/profile/edit").session(session)
                        .param("name", "Admin")
                        .param("email", "other@test.com")
                        .param("username", "admin")
                        .param("phone", "11999999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile/edit*"));
    }

    @Test
    void testEditUserWithDuplicateEmail() throws Exception {
        // Criar outro usuário
        User otherUser = new User();
        otherUser.setName("Other User");
        otherUser.setEmail("other@test.com");
        otherUser.setUsername("otheruser");
        otherUser.setPasswordHash(passwordEncoder.encode("password123"));
        otherUser.setIsActive(true);
        otherUser = userRepository.save(otherUser);

        // Tentar atualizar admin com email do outro usuário
        mockMvc.perform(post("/users/" + admin.getId() + "/edit").session(session)
                        .param("name", "Admin")
                        .param("email", "other@test.com")
                        .param("username", "admin")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/users/" + admin.getId() + "/edit*"));
    }

    @Test
    void testEditUserWithPassword() throws Exception {
        mockMvc.perform(post("/users/" + admin.getId() + "/edit").session(session)
                        .param("name", "Admin Updated")
                        .param("email", "admin@test.com")
                        .param("username", "admin")
                        .param("password", "newpassword123")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testCreateUserPage() throws Exception {
        mockMvc.perform(get("/users/create").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("users/create"));
    }

    @Test
    void testApiListWithoutPagination() throws Exception {
        // Criar alguns usuários
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("Test User " + i);
            user.setEmail("test" + i + "@test.com");
            user.setUsername("test" + i);
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setIsActive(true);
            userRepository.save(user);
        }

        mockMvc.perform(get("/users/api").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(6)); // 5 + admin
    }
}

