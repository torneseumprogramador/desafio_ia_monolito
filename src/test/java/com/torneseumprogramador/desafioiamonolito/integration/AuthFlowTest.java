package com.torneseumprogramador.desafioiamonolito.integration;

import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para fluxo completo de autenticação
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        // Limpar usuários de teste
        userRepository.deleteAll();
    }

    @Test
    void testCompleteAuthFlow() throws Exception {
        // 1. Acessar página de registro
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        // 2. Registrar novo usuário
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                        .param("name", "Integration User")
                        .param("email", "integration@test.com")
                        .param("username", "integrationuser")
                        .param("password", "password123")
                        .param("password_confirm", "password123")
                        .param("phone", "11999999999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        // 3. Verificar que foi criada uma sessão
        var session = registerResult.getRequest().getSession();
        assert session != null;
        assert session.getAttribute("user_id") != null;

        // 4. Logout
        mockMvc.perform(get("/auth/logout").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // 5. Login novamente
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "integrationuser")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testLoginWithEmail() throws Exception {
        // Criar usuário primeiro
        mockMvc.perform(post("/auth/register")
                        .param("name", "Email Test")
                        .param("email", "email@test.com")
                        .param("username", "emailtest")
                        .param("password", "password123")
                        .param("password_confirm", "password123"))
                .andExpect(status().is3xxRedirection());

        // Fazer logout
        mockMvc.perform(get("/auth/logout"));

        // Login com email
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "email@test.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("username_or_email", "nonexistent")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void testRegisterWithDuplicateEmail() throws Exception {
        // Criar primeiro usuário
        mockMvc.perform(post("/auth/register")
                        .param("name", "First User")
                        .param("email", "duplicate@test.com")
                        .param("username", "firstuser")
                        .param("password", "password123")
                        .param("password_confirm", "password123"))
                .andExpect(status().is3xxRedirection());

        // Tentar criar segundo usuário com mesmo email
        mockMvc.perform(post("/auth/register")
                        .param("name", "Second User")
                        .param("email", "duplicate@test.com")
                        .param("username", "seconduser")
                        .param("password", "password123")
                        .param("password_confirm", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"));
    }

    @Test
    void testRegisterWithPasswordMismatch() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("name", "Test User")
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("password_confirm", "different"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"));
    }

    @Test
    void testRegisterWithShortPassword() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("name", "Test User")
                        .param("email", "test@test.com")
                        .param("username", "testuser")
                        .param("password", "123")
                        .param("password_confirm", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"));
    }
}

