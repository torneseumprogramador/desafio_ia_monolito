package com.torneseumprogramador.desafioiamonolito.services;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getUserGreeting(String username) {
        return "Olá, " + username + "! Bem-vindo ao sistema!";
    }
}
