package com.torneseumprogramador.desafioiamonolito.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor para autenticação
 * Equivalente ao middleware login_required do Python
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Verificar se o usuário está logado
        if (session == null || session.getAttribute("user_id") == null) {
            // Salvar a URL que o usuário estava tentando acessar
            String requestURI = request.getRequestURI();
            String queryString = request.getQueryString();
            String targetUrl = queryString != null ? requestURI + "?" + queryString : requestURI;
            
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("next", targetUrl);
            newSession.setAttribute("flash_message", "Você precisa estar logado para acessar esta página.");
            newSession.setAttribute("flash_type", "warning");
            
            response.sendRedirect("/auth/login");
            return false;
        }
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Adicionar dados do usuário ao modelo se disponível
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object userId = session.getAttribute("user_id");
                if (userId != null) {
                    modelAndView.addObject("logged_in", true);
                    modelAndView.addObject("user_id", userId);
                    modelAndView.addObject("username", session.getAttribute("username"));
                    modelAndView.addObject("user_name", session.getAttribute("name"));
                } else {
                    modelAndView.addObject("logged_in", false);
                }
            }
        }
    }
}

