package com.torneseumprogramador.desafioiamonolito.integration;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import com.torneseumprogramador.desafioiamonolito.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para gerenciamento de usuários
 */
@SpringBootTest
class UserManagementTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // Limpar dados de teste
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Limpar dados após testes
        if (testUserId != null) {
            userRepository.deleteById(testUserId);
        }
    }

    @Test
    void testCompleteUserLifecycle() {
        // 1. Criar usuário
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Integration Test User");
        userData.put("email", "integration@test.com");
        userData.put("username", "integrationtest");
        userData.put("password", "password123");
        userData.put("phone", "11999999999");
        userData.put("isActive", true);

        Object[] createResult = userService.createUser(userData);
        User createdUser = (User) createResult[0];
        String createError = (String) createResult[1];

        assertNull(createError, "Não deve haver erro ao criar usuário");
        assertNotNull(createdUser, "Usuário deve ser criado");
        assertNotNull(createdUser.getId(), "ID deve ser gerado");
        testUserId = createdUser.getId();

        // 2. Buscar usuário
        Optional<User> foundUser = userService.getUserById(testUserId);
        assertTrue(foundUser.isPresent(), "Usuário deve ser encontrado");
        assertEquals("integration@test.com", foundUser.get().getEmail());

        // 3. Autenticar usuário
        Object[] authResult = userService.authenticate("integrationtest", "password123");
        User authenticatedUser = (User) authResult[0];
        String authError = (String) authResult[1];

        assertNull(authError, "Não deve haver erro na autenticação");
        assertNotNull(authenticatedUser, "Usuário deve ser autenticado");

        // 4. Atualizar usuário
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated Name");
        updateData.put("phone", "11888888888");

        Object[] updateResult = userService.updateUser(testUserId, updateData);
        User updatedUser = (User) updateResult[0];
        String updateError = (String) updateResult[1];

        assertNull(updateError, "Não deve haver erro ao atualizar");
        assertNotNull(updatedUser, "Usuário deve ser atualizado");
        assertEquals("Updated Name", updatedUser.getName());

        // 5. Toggle status
        Object[] toggleResult = userService.toggleUserStatus(testUserId);
        User toggledUser = (User) toggleResult[0];

        assertNotNull(toggledUser, "Usuário deve ter status alterado");
        assertFalse(toggledUser.getIsActive(), "Usuário deve estar inativo");

        // 6. Deletar usuário
        Object[] deleteResult = userService.deleteUser(testUserId);
        boolean deleteSuccess = (boolean) deleteResult[0];

        assertTrue(deleteSuccess, "Usuário deve ser deletado com sucesso");

        // 7. Verificar que foi deletado
        Optional<User> deletedUser = userService.getUserById(testUserId);
        assertFalse(deletedUser.isPresent(), "Usuário não deve mais existir");

        testUserId = null; // Marcar como deletado
    }

    @Test
    void testDuplicateEmailValidation() {
        // Criar primeiro usuário
        Map<String, Object> userData1 = new HashMap<>();
        userData1.put("name", "User One");
        userData1.put("email", "duplicate@test.com");
        userData1.put("username", "userone");
        userData1.put("password", "password123");

        Object[] result1 = userService.createUser(userData1);
        User user1 = (User) result1[0];
        assertNotNull(user1);
        testUserId = user1.getId();

        // Tentar criar segundo usuário com mesmo email
        Map<String, Object> userData2 = new HashMap<>();
        userData2.put("name", "User Two");
        userData2.put("email", "duplicate@test.com");
        userData2.put("username", "usertwo");
        userData2.put("password", "password123");

        Object[] result2 = userService.createUser(userData2);
        User user2 = (User) result2[0];
        String error2 = (String) result2[1];

        assertNull(user2, "Segundo usuário não deve ser criado");
        assertEquals("Email já cadastrado", error2);
    }

    @Test
    void testPasswordHashing() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Password Test");
        userData.put("email", "password@test.com");
        userData.put("username", "passwordtest");
        userData.put("password", "plaintext123");

        Object[] result = userService.createUser(userData);
        User user = (User) result[0];
        assertNotNull(user);
        testUserId = user.getId();

        // Verificar que a senha foi hasheada
        assertNotEquals("plaintext123", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("plaintext123", user.getPasswordHash()));
    }

    @Test
    void testGetUserStatistics() {
        // Criar alguns usuários de teste
        for (int i = 0; i < 3; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "Stats User " + i);
            userData.put("email", "stats" + i + "@test.com");
            userData.put("username", "statsuser" + i);
            userData.put("password", "password123");

            userService.createUser(userData);
        }

        Map<String, Object> stats = userService.getUserStatistics();

        assertNotNull(stats);
        assertTrue((Long) stats.get("total_users") >= 3);
        assertTrue((Long) stats.get("active_users") >= 3);
    }
}

