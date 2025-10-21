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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de fluxo completo da aplicação
 */
@SpringBootTest
@AutoConfigureMockMvc
class CompleteFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCompleteUserJourney() throws Exception {
        // 1. Tentar acessar área protegida sem login - deve redirecionar
        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        // 2. Registrar novo usuário
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .param("name", "Journey User")
                        .param("email", "journey@test.com")
                        .param("username", "journeyuser")
                        .param("password", "password123")
                        .param("password_confirm", "password123")
                        .param("phone", "11999999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        // 3. Verificar sessão criada
        MockHttpSession session = (MockHttpSession) registerResult.getRequest().getSession();
        assertNotNull(session);
        assertNotNull(session.getAttribute("user_id"));
        Long userId = (Long) session.getAttribute("user_id");

        // 4. Acessar API de estatísticas (autenticado)
        mockMvc.perform(get("/api/stats").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_users").exists());

        // 5. Criar mais usuários via CRUD
        mockMvc.perform(post("/users/create").session(session)
                        .param("name", "Second User")
                        .param("email", "second@test.com")
                        .param("username", "seconduser")
                        .param("password", "password123")
                        .param("phone", "11888888888")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection());

        // 6. Listar usuários via API
        mockMvc.perform(get("/users/api").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.users").isArray());

        // 7. Alterar próprio perfil
        mockMvc.perform(post("/profile/edit").session(session)
                        .param("name", "Journey User Updated")
                        .param("email", "journey@test.com")
                        .param("username", "journeyuser")
                        .param("phone", "11777777777"))
                .andExpect(status().is3xxRedirection());

        // 8. Alterar senha
        mockMvc.perform(post("/profile/change-password").session(session)
                        .param("current_password", "password123")
                        .param("new_password", "newpassword456")
                        .param("confirm_password", "newpassword456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 9. Logout
        mockMvc.perform(get("/auth/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // 10. Login com nova senha
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "journeyuser")
                        .param("password", "newpassword456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // 11. Verificar que não consegue fazer login com senha antiga
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "journeyuser")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void testUserManipulation() throws Exception {
        // Criar admin
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setIsActive(true);
        admin = userRepository.save(admin);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user_id", admin.getId());
        session.setAttribute("username", admin.getUsername());
        session.setAttribute("name", admin.getName());

        // Criar usuário via CRUD
        MvcResult createResult = mockMvc.perform(post("/users/create").session(session)
                        .param("name", "Managed User")
                        .param("email", "managed@test.com")
                        .param("username", "manageduser")
                        .param("password", "password123")
                        .param("phone", "11999999999")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // Buscar usuário criado
        User managedUser = userRepository.findByUsername("manageduser").orElse(null);
        assertNotNull(managedUser);

        // Buscar usuário via API
        mockMvc.perform(get("/users/api/" + managedUser.getId()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("manageduser"));

        // Editar usuário
        mockMvc.perform(post("/users/" + managedUser.getId() + "/edit").session(session)
                        .param("name", "Updated Managed User")
                        .param("email", "managed@test.com")
                        .param("username", "manageduser")
                        .param("phone", "11888888888")
                        .param("is_active", "true"))
                .andExpect(status().is3xxRedirection());

        // Desativar usuário
        mockMvc.perform(post("/users/" + managedUser.getId() + "/toggle-status").session(session))
                .andExpect(status().is3xxRedirection());

        // Verificar que está inativo
        User inactiveUser = userRepository.findById(managedUser.getId()).orElse(null);
        assertNotNull(inactiveUser);
        assertFalse(inactiveUser.getIsActive());

        // Reativar usuário
        mockMvc.perform(post("/users/" + managedUser.getId() + "/toggle-status").session(session))
                .andExpect(status().is3xxRedirection());

        // Verificar que está ativo novamente
        User activeUser = userRepository.findById(managedUser.getId()).orElse(null);
        assertNotNull(activeUser);
        assertTrue(activeUser.getIsActive());

        // Deletar usuário
        mockMvc.perform(post("/users/" + managedUser.getId() + "/delete").session(session))
                .andExpect(status().is3xxRedirection());

        // Verificar que foi deletado
        User deletedUser = userRepository.findById(managedUser.getId()).orElse(null);
        assertNull(deletedUser);
    }

    @Test
    void testAccessControlFlow() throws Exception {
        // Criar usuário inativo
        User inactiveUser = new User();
        inactiveUser.setName("Inactive");
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setUsername("inactive");
        inactiveUser.setPasswordHash(passwordEncoder.encode("password123"));
        inactiveUser.setIsActive(false);
        inactiveUser = userRepository.save(inactiveUser);

        // Tentar login com usuário inativo
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "inactive")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void testPaginationFlow() throws Exception {
        // Criar admin
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setIsActive(true);
        admin = userRepository.save(admin);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user_id", admin.getId());

        // Criar 25 usuários para testar paginação
        for (int i = 1; i <= 25; i++) {
            mockMvc.perform(post("/users/create").session(session)
                            .param("name", "User " + i)
                            .param("email", "user" + i + "@test.com")
                            .param("username", "user" + i)
                            .param("password", "password123")
                            .param("is_active", "true"))
                    .andExpect(status().is3xxRedirection());
        }

        // Verificar total via API
        MvcResult apiResult = mockMvc.perform(get("/users/api").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists())
                .andReturn();
    }
}

