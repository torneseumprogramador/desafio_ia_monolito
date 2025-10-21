package com.torneseumprogramador.desafioiamonolito.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para rotas que só devem ser acessadas por visitantes (não autenticados)
 * Equivalente ao middleware guest_only do Python
 */
@Component
public class GuestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Se o usuário já estiver logado, redirecionar para home
        if (session != null && session.getAttribute("user_id") != null) {
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("flash_message", "Você já está logado!");
            newSession.setAttribute("flash_type", "info");
            
            response.sendRedirect("/");
            return false;
        }
        
        return true;
    }
}

