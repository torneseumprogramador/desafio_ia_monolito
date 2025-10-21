package com.torneseumprogramador.desafioiamonolito.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestInterceptorTest {

    @InjectMocks
    private GuestInterceptor guestInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private final Object handler = new Object();

    @Test
    void testPreHandle_NotAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        boolean result = guestInterceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    void testPreHandle_Authenticated() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(1L);

        boolean result = guestInterceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(response).sendRedirect("/");
        verify(session).setAttribute("flash_message", "Você já está logado!");
        verify(session).setAttribute("flash_type", "info");
    }

    @Test
    void testPreHandle_SessionExistsButNotLoggedIn() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user_id")).thenReturn(null);

        boolean result = guestInterceptor.preHandle(request, response, handler);

        assertTrue(result);
        verify(response, never()).sendRedirect(anyString());
    }
}

