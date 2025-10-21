package com.torneseumprogramador.desafioiamonolito.repositories;

import com.torneseumprogramador.desafioiamonolito.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setPhone("11999999999");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("test@example.com");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmailOrUsername_ByEmail() {
        Optional<User> found = userRepository.findByEmailOrUsername("test@example.com", "nonexistent");
        assertTrue(found.isPresent());
    }

    @Test
    void testFindByEmailOrUsername_ByUsername() {
        Optional<User> found = userRepository.findByEmailOrUsername("nonexistent@example.com", "testuser");
        assertTrue(found.isPresent());
    }

    @Test
    void testFindByIsActiveTrueOrderByCreatedAtDesc() {
        List<User> activeUsers = userRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        assertFalse(activeUsers.isEmpty());
        assertEquals(1, activeUsers.size());
    }

    @Test
    void testFindAllByOrderByCreatedAtDesc() {
        Page<User> page = userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmailExcludingId() {
        assertFalse(userRepository.existsByEmailExcludingId("test@example.com", testUser.getId()));
        assertTrue(userRepository.existsByEmailExcludingId("test@example.com", 999L));
    }

    @Test
    void testExistsByUsernameExcludingId() {
        assertFalse(userRepository.existsByUsernameExcludingId("testuser", testUser.getId()));
        assertTrue(userRepository.existsByUsernameExcludingId("testuser", 999L));
    }

    @Test
    void testCountByIsActiveTrue() {
        long count = userRepository.countByIsActiveTrue();
        assertEquals(1, count);
        
        testUser.setIsActive(false);
        userRepository.save(testUser);
        
        count = userRepository.countByIsActiveTrue();
        assertEquals(0, count);
    }

    @Test
    void testCountByCreatedAtAfter() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        long count = userRepository.countByCreatedAtAfter(yesterday);
        assertEquals(1, count);
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        count = userRepository.countByCreatedAtAfter(tomorrow);
        assertEquals(0, count);
    }

    @Test
    void testCountByCreatedAtBetween() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        
        long count = userRepository.countByCreatedAtBetween(yesterday, tomorrow);
        assertEquals(1, count);
    }

    @Test
    void testCreateAndFindUser() {
        User newUser = new User();
        newUser.setName("Another User");
        newUser.setEmail("another@example.com");
        newUser.setUsername("anotheruser");
        newUser.setPasswordHash("$2a$10$hashedpassword");
        newUser.setIsActive(true);
        
        User saved = userRepository.save(newUser);
        assertNotNull(saved.getId());
        
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Another User", found.get().getName());
    }

    @Test
    void testUpdateUser() {
        testUser.setName("Updated Name");
        User updated = userRepository.save(testUser);
        
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void testDeleteUser() {
        Long userId = testUser.getId();
        userRepository.delete(testUser);
        
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }
}

