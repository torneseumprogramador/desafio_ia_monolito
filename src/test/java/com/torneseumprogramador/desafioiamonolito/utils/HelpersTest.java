package com.torneseumprogramador.desafioiamonolito.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Helpers
 */
class HelpersTest {

    @Test
    void testFormatDate() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 21, 10, 30);
        String formatted = Helpers.formatDate(date);
        assertEquals("21/10/2025", formatted);
    }

    @Test
    void testFormatDateWithCustomFormat() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 21, 10, 30);
        String formatted = Helpers.formatDate(date, "yyyy-MM-dd");
        assertEquals("2025-10-21", formatted);
    }

    @Test
    void testFormatDateNull() {
        String formatted = Helpers.formatDate(null);
        assertEquals("", formatted);
    }

    @Test
    void testValidateEmail_Valid() {
        assertTrue(Helpers.validateEmail("test@example.com"));
        assertTrue(Helpers.validateEmail("user.name@domain.co.uk"));
        assertTrue(Helpers.validateEmail("firstname+lastname@example.com"));
    }

    @Test
    void testValidateEmail_Invalid() {
        assertFalse(Helpers.validateEmail("invalid"));
        assertFalse(Helpers.validateEmail("@example.com"));
        assertFalse(Helpers.validateEmail("test@"));
        assertFalse(Helpers.validateEmail(""));
        assertFalse(Helpers.validateEmail(null));
    }

    @Test
    void testSlugify() {
        assertEquals("hello-world", Helpers.slugify("Hello World"));
        assertEquals("test-123", Helpers.slugify("Test 123"));
        assertEquals("special-chars", Helpers.slugify("Special!@#$%Chars"));
        assertEquals("", Helpers.slugify(""));
        assertEquals("", Helpers.slugify(null));
    }

    @Test
    void testTruncate() {
        String text = "This is a long text";
        assertEquals("This...", Helpers.truncate(text, 4));
        assertEquals(text, Helpers.truncate(text, 50));
        assertEquals("", Helpers.truncate(null, 10));
    }

    @Test
    void testIsNullOrEmpty() {
        assertTrue(Helpers.isNullOrEmpty(null));
        assertTrue(Helpers.isNullOrEmpty(""));
        assertTrue(Helpers.isNullOrEmpty("   "));
        assertFalse(Helpers.isNullOrEmpty("text"));
    }

    @Test
    void testCoalesce() {
        assertEquals("first", Helpers.coalesce("first", "second"));
        assertEquals("second", Helpers.coalesce(null, "second"));
        assertEquals("third", Helpers.coalesce(null, null, "third"));
        assertNull(Helpers.coalesce(null, null, null));
    }
}

