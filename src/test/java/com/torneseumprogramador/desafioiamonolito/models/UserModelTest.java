package com.torneseumprogramador.desafioiamonolito.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes adicionais para o modelo User
 */
class UserModelTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testAllSettersAndGetters() {
        user.setId(10L);
        user.setName("Full Test");
        user.setEmail("full@test.com");
        user.setUsername("fulltest");
        user.setPasswordHash("$2a$10$hash");
        user.setPhone("11999999999");
        user.setIsActive(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(10L, user.getId());
        assertEquals("Full Test", user.getName());
        assertEquals("full@test.com", user.getEmail());
        assertEquals("fulltest", user.getUsername());
        assertEquals("$2a$10$hash", user.getPasswordHash());
        assertEquals("11999999999", user.getPhone());
        assertTrue(user.getIsActive());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        User fullUser = new User(1L, "Test", "test@test.com", "test", "$2a$10$hash", 
                                 "11999999999", true, now, now);

        assertEquals(1L, fullUser.getId());
        assertEquals("Test", fullUser.getName());
        assertEquals("test@test.com", fullUser.getEmail());
        assertEquals("test", fullUser.getUsername());
        assertEquals("$2a$10$hash", fullUser.getPasswordHash());
        assertEquals("11999999999", fullUser.getPhone());
        assertTrue(fullUser.getIsActive());
        assertEquals(now, fullUser.getCreatedAt());
        assertEquals(now, fullUser.getUpdatedAt());
    }

    @Test
    void testDefaultConstructor() {
        User newUser = new User();
        assertNull(newUser.getId());
        assertTrue(newUser.getIsActive()); // Default é true
    }

    @Test
    void testToDictWithNullDates() {
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setUsername("test");
        user.setPhone("11999999999");
        user.setIsActive(true);
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        Map<String, Object> dict = user.toDict();

        assertNull(dict.get("createdAt"));
        assertNull(dict.get("updatedAt"));
    }

    @Test
    void testToDictWithDates() {
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setUsername("test");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        Map<String, Object> dict = user.toDict();

        assertNotNull(dict.get("createdAt"));
        assertNotNull(dict.get("updatedAt"));
        assertTrue(dict.get("createdAt").toString().contains(String.valueOf(now.getYear())));
    }

    @Test
    void testUpdateFromMapPartial() {
        user.setName("Original");
        user.setEmail("original@test.com");
        user.setUsername("original");
        user.setPhone("11111111111");
        user.setIsActive(true);

        Map<String, Object> data = new HashMap<>();
        data.put("name", "Updated");
        data.put("phone", "22222222222");

        user.updateFromMap(data);

        assertEquals("Updated", user.getName());
        assertEquals("22222222222", user.getPhone());
        assertEquals("original@test.com", user.getEmail()); // Não alterado
        assertEquals("original", user.getUsername()); // Não alterado
    }

    @Test
    void testUpdateFromMapComplete() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Complete Update");
        data.put("email", "complete@test.com");
        data.put("username", "complete");
        data.put("phone", "11999999999");
        data.put("isActive", false);

        user.updateFromMap(data);

        assertEquals("Complete Update", user.getName());
        assertEquals("complete@test.com", user.getEmail());
        assertEquals("complete", user.getUsername());
        assertEquals("11999999999", user.getPhone());
        assertFalse(user.getIsActive());
    }

    @Test
    void testUpdateFromMapEmpty() {
        user.setName("Original");
        user.setEmail("original@test.com");

        Map<String, Object> data = new HashMap<>();
        user.updateFromMap(data);

        assertEquals("Original", user.getName());
        assertEquals("original@test.com", user.getEmail());
    }

    @Test
    void testSetPasswordMethod() {
        user.setPassword("$2a$10$newhash");
        assertEquals("$2a$10$newhash", user.getPasswordHash());
    }

    @Test
    void testToDictDoesNotIncludePassword() {
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setUsername("test");
        user.setPasswordHash("$2a$10$hash");

        Map<String, Object> dict = user.toDict();

        assertFalse(dict.containsKey("passwordHash"));
        assertFalse(dict.containsKey("password"));
    }
}

