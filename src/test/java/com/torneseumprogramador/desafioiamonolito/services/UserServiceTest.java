package com.torneseumprogramador.desafioiamonolito.services;

import com.torneseumprogramador.desafioiamonolito.models.User;
import com.torneseumprogramador.desafioiamonolito.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Map<String, Object> validUserData;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validUserData = new HashMap<>();
        validUserData.put("name", "Test User");
        validUserData.put("email", "test@example.com");
        validUserData.put("username", "testuser");
        validUserData.put("password", "password123");
        validUserData.put("phone", "11999999999");
        validUserData.put("isActive", true);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setPasswordHash("$2a$10$hashedpassword");
        mockUser.setIsActive(true);
    }

    @Test
    void testCreateUser_Success() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(repository.save(any(User.class))).thenReturn(mockUser);

        Object[] result = userService.createUser(validUserData);

        assertNotNull(result[0]);
        assertNull(result[1]);
        assertTrue(result[0] instanceof User);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(repository.existsByEmail(anyString())).thenReturn(true);

        Object[] result = userService.createUser(validUserData);

        assertNull(result[0]);
        assertEquals("Email já cadastrado", result[1]);
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.existsByUsername(anyString())).thenReturn(true);

        Object[] result = userService.createUser(validUserData);

        assertNull(result[0]);
        assertEquals("Username já cadastrado", result[1]);
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_MissingName() {
        validUserData.remove("name");

        Object[] result = userService.createUser(validUserData);

        assertNull(result[0]);
        assertEquals("Nome é obrigatório", result[1]);
    }

    @Test
    void testGetUserById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testAuthenticate_Success() {
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Object[] result = userService.authenticate("testuser", "password123");

        assertNotNull(result[0]);
        assertNull(result[1]);
        assertTrue(result[0] instanceof User);
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Object[] result = userService.authenticate("testuser", "wrongpassword");

        assertNull(result[0]);
        assertEquals("Usuário ou senha inválidos", result[1]);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        Object[] result = userService.authenticate("nonexistent", "password123");

        assertNull(result[0]);
        assertEquals("Usuário ou senha inválidos", result[1]);
    }

    @Test
    void testAuthenticate_InactiveUser() {
        mockUser.setIsActive(false);
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        Object[] result = userService.authenticate("testuser", "password123");

        assertNull(result[0]);
        assertEquals("Usuário inativo. Entre em contato com o administrador.", result[1]);
    }

    @Test
    void testDeleteUser_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(repository).delete(any(User.class));

        Object[] result = userService.deleteUser(1L);

        assertEquals(true, result[0]);
        assertNull(result[1]);
        verify(repository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Object[] result = userService.deleteUser(1L);

        assertEquals(false, result[0]);
        assertEquals("Usuário não encontrado", result[1]);
        verify(repository, never()).delete(any(User.class));
    }

    @Test
    void testToggleUserStatus() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(repository.save(any(User.class))).thenReturn(mockUser);

        boolean initialStatus = mockUser.getIsActive();
        Object[] result = userService.toggleUserStatus(1L);

        assertNotNull(result[0]);
        assertNull(result[1]);
        User updatedUser = (User) result[0];
        assertEquals(!initialStatus, updatedUser.getIsActive());
    }
}

