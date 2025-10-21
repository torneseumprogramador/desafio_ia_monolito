package com.torneseumprogramador.desafioiamonolito.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Classe com métodos auxiliares
 */
public class Helpers {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Formata uma data
     * @param date Data a formatar
     * @param format Formato desejado (padrão: dd/MM/yyyy)
     * @return String com a data formatada
     */
    public static String formatDate(LocalDateTime date, String format) {
        if (date == null) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "dd/MM/yyyy";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * Formata uma data com formato padrão (dd/MM/yyyy)
     * @param date Data a formatar
     * @return String com a data formatada
     */
    public static String formatDate(LocalDateTime date) {
        return formatDate(date, "dd/MM/yyyy");
    }

    /**
     * Valida formato de email básico
     * @param email Email a validar
     * @return true se válido, false caso contrário
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Converte texto em slug URL-friendly
     * @param text Texto a converter
     * @return Slug gerado
     */
    public static String slugify(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        text = text.toLowerCase();
        text = text.replaceAll("[^a-z0-9]+", "-");
        text = text.replaceAll("^-+|-+$", "");
        
        return text;
    }

    /**
     * Trunca um texto para um tamanho máximo
     * @param text Texto a truncar
     * @param maxLength Tamanho máximo
     * @return Texto truncado
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Verifica se uma string é nula ou vazia
     * @param str String a verificar
     * @return true se nula ou vazia, false caso contrário
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Retorna o primeiro valor não nulo
     * @param values Valores a verificar
     * @return Primeiro valor não nulo ou null se todos forem nulos
     */
    @SafeVarargs
    public static <T> T coalesce(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}

