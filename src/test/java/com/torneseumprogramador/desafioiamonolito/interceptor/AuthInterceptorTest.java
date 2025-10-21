package com.torneseumprogramador.desafioiamonolito.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @InjectMocks
    private AuthInterceptor authInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private final Object handler = new Object();

    @BeforeEach
    void setUp() {
    }

    @Test
    void testPreHandle_WithAuthenticatedUser() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(1L);

        boolean result = authInterceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testPreHandle_WithoutAuthenticatedUser() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(request.getRequestURI()).thenReturn("/users");

        boolean result = authInterceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(response).sendRedirect("/auth/login");
        verify(session).setAttribute("flash_message", "Você precisa estar logado para acessar esta página.");
        verify(session).setAttribute("flash_type", "warning");
    }

    @Test
    void testPreHandle_NoSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/dashboard");
        when(request.getQueryString()).thenReturn("param=value");

        boolean result = authInterceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(session).setAttribute(eq("next"), eq("/dashboard?param=value"));
        verify(response).sendRedirect("/auth/login");
    }

    @Test
    void testPostHandle_WithAuthenticatedUser() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(1L);
        when(session.getAttribute("username")).thenReturn("testuser");
        when(session.getAttribute("name")).thenReturn("Test User");

        authInterceptor.postHandle(request, response, handler, modelAndView);

        assertEquals(true, modelAndView.getModel().get("logged_in"));
        assertEquals(1L, modelAndView.getModel().get("user_id"));
        assertEquals("testuser", modelAndView.getModel().get("username"));
    }

    @Test
    void testPostHandle_WithoutAuthenticatedUser() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(null);

        authInterceptor.postHandle(request, response, handler, modelAndView);

        assertEquals(false, modelAndView.getModel().get("logged_in"));
        assertNull(modelAndView.getModel().get("user_id"));
    }

    @Test
    void testPostHandle_NullModelAndView() throws Exception {
        // Não deve lançar exceção
        authInterceptor.postHandle(request, response, handler, null);
    }
}

