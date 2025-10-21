package com.torneseumprogramador.desafioiamonolito.services;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes adicionais para UserService focando em validações e casos de borda
 */
@SpringBootTest
class UserServiceValidationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_EmptyName() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "");
        data.put("email", "test@test.com");
        data.put("username", "testuser");
        data.put("password", "password123");

        Object[] result = userService.createUser(data);
        assertNull(result[0]);
        assertEquals("Nome é obrigatório", result[1]);
    }

    @Test
    void testCreateUser_EmptyEmail() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("email", "");
        data.put("username", "testuser");
        data.put("password", "password123");

        Object[] result = userService.createUser(data);
        assertNull(result[0]);
        assertEquals("Email é obrigatório", result[1]);
    }

    @Test
    void testCreateUser_EmptyUsername() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("email", "test@test.com");
        data.put("username", "");
        data.put("password", "password123");

        Object[] result = userService.createUser(data);
        assertNull(result[0]);
        assertEquals("Username é obrigatório", result[1]);
    }

    @Test
    void testCreateUser_EmptyPassword() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Test User");
        data.put("email", "test@test.com");
        data.put("username", "testuser");
        data.put("password", "");

        Object[] result = userService.createUser(data);
        assertNull(result[0]);
        assertEquals("Senha é obrigatória", result[1]);
    }

    @Test
    void testCreateUser_MissingFields() {
        Map<String, Object> data = new HashMap<>();
        
        Object[] result = userService.createUser(data);
        assertNull(result[0]);
        assertNotNull(result[1]);
    }

    @Test
    void testUpdateUser_NotFound() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Updated Name");

        Object[] result = userService.updateUser(999L, data);
        assertNull(result[0]);
        assertEquals("Usuário não encontrado", result[1]);
    }

    @Test
    void testUpdateUser_DuplicateEmail() {
        // Criar primeiro usuário
        Map<String, Object> user1Data = new HashMap<>();
        user1Data.put("name", "User 1");
        user1Data.put("email", "user1@test.com");
        user1Data.put("username", "user1");
        user1Data.put("password", "password123");
        userService.createUser(user1Data);

        // Criar segundo usuário
        Map<String, Object> user2Data = new HashMap<>();
        user2Data.put("name", "User 2");
        user2Data.put("email", "user2@test.com");
        user2Data.put("username", "user2");
        user2Data.put("password", "password123");
        Object[] result2 = userService.createUser(user2Data);
        User user2 = (User) result2[0];

        // Tentar atualizar user2 com email do user1
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("email", "user1@test.com");

        Object[] updateResult = userService.updateUser(user2.getId(), updateData);
        assertNull(updateResult[0]);
        assertEquals("Email já cadastrado", updateResult[1]);
    }

    @Test
    void testUpdateUser_DuplicateUsername() {
        // Criar dois usuários
        Map<String, Object> user1Data = new HashMap<>();
        user1Data.put("name", "User 1");
        user1Data.put("email", "user1@test.com");
        user1Data.put("username", "user1");
        user1Data.put("password", "password123");
        userService.createUser(user1Data);

        Map<String, Object> user2Data = new HashMap<>();
        user2Data.put("name", "User 2");
        user2Data.put("email", "user2@test.com");
        user2Data.put("username", "user2");
        user2Data.put("password", "password123");
        Object[] result2 = userService.createUser(user2Data);
        User user2 = (User) result2[0];

        // Tentar atualizar user2 com username do user1
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("username", "user1");

        Object[] updateResult = userService.updateUser(user2.getId(), updateData);
        assertNull(updateResult[0]);
        assertEquals("Username já cadastrado", updateResult[1]);
    }

    @Test
    void testUpdateUser_PasswordUpdate() {
        // Criar usuário
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Test User");
        userData.put("email", "test@test.com");
        userData.put("username", "testuser");
        userData.put("password", "password123");
        Object[] createResult = userService.createUser(userData);
        User user = (User) createResult[0];
        String oldPasswordHash = user.getPasswordHash();

        // Atualizar senha
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("password", "newpassword123");

        Object[] updateResult = userService.updateUser(user.getId(), updateData);
        User updatedUser = (User) updateResult[0];

        assertNotNull(updatedUser);
        assertNotEquals(oldPasswordHash, updatedUser.getPasswordHash());
    }

    @Test
    void testGetAllUsers_Pagination() {
        // Criar vários usuários
        for (int i = 0; i < 15; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "User " + i);
            userData.put("email", "user" + i + "@test.com");
            userData.put("username", "user" + i);
            userData.put("password", "password123");
            userService.createUser(userData);
        }

        // Testar primeira página
        Object[] result1 = userService.getAllUsers(1, 10);
        @SuppressWarnings("unchecked")
        List<User> page1 = (List<User>) result1[0];
        long total = (long) result1[1];

        assertEquals(10, page1.size());
        assertEquals(15, total);

        // Testar segunda página
        Object[] result2 = userService.getAllUsers(2, 10);
        @SuppressWarnings("unchecked")
        List<User> page2 = (List<User>) result2[0];

        assertEquals(5, page2.size());
    }

    @Test
    void testGetActiveUsers() {
        // Criar usuários ativos e inativos
        for (int i = 0; i < 3; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "Active User " + i);
            userData.put("email", "active" + i + "@test.com");
            userData.put("username", "active" + i);
            userData.put("password", "password123");
            userData.put("isActive", true);
            userService.createUser(userData);
        }

        for (int i = 0; i < 2; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "Inactive User " + i);
            userData.put("email", "inactive" + i + "@test.com");
            userData.put("username", "inactive" + i);
            userData.put("password", "password123");
            userData.put("isActive", false);
            userService.createUser(userData);
        }

        List<User> activeUsers = userService.getActiveUsers();
        assertEquals(3, activeUsers.size());
    }

    @Test
    void testCountUsers() {
        // Criar alguns usuários
        for (int i = 0; i < 5; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "User " + i);
            userData.put("email", "user" + i + "@test.com");
            userData.put("username", "user" + i);
            userData.put("password", "password123");
            userService.createUser(userData);
        }

        long count = userService.countUsers();
        assertEquals(5, count);
    }

    @Test
    void testGetMonthlyUserRegistrations() {
        // Criar alguns usuários
        for (int i = 0; i < 3; i++) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", "User " + i);
            userData.put("email", "user" + i + "@test.com");
            userData.put("username", "user" + i);
            userData.put("password", "password123");
            userService.createUser(userData);
        }

        List<Map<String, Object>> monthlyData = userService.getMonthlyUserRegistrations(6);
        assertNotNull(monthlyData);
        assertFalse(monthlyData.isEmpty());
    }
}

