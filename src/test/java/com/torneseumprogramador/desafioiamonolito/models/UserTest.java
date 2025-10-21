package com.torneseumprogramador.desafioiamonolito.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o modelo User
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setPhone("11999999999");
        user.setIsActive(true);
    }

    @Test
    void testToDict() {
        Map<String, Object> dict = user.toDict();

        assertNotNull(dict);
        assertEquals(1L, dict.get("id"));
        assertEquals("Test User", dict.get("name"));
        assertEquals("test@example.com", dict.get("email"));
        assertEquals("testuser", dict.get("username"));
        assertEquals("11999999999", dict.get("phone"));
        assertEquals(true, dict.get("isActive"));
        assertFalse(dict.containsKey("passwordHash")); // Senha não deve estar no dict
    }

    @Test
    void testUpdateFromMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Updated Name");
        data.put("email", "updated@example.com");
        data.put("phone", "11888888888");

        user.updateFromMap(data);

        assertEquals("Updated Name", user.getName());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("11888888888", user.getPhone());
        assertEquals("testuser", user.getUsername()); // Não alterado
    }

    @Test
    void testSetPassword() {
        String newPassword = "newpassword123";
        user.setPassword(newPassword);

        assertNotEquals(newPassword, user.getPasswordHash());
        assertNotNull(user.getPasswordHash());
    }

    @Test
    void testToString() {
        String result = user.toString();
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("1"));
    }
}

