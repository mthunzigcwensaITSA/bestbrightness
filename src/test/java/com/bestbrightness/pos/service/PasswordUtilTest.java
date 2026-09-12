package com.bestbrightness.pos.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void matchesTheOriginalPassword() {
        String hash = PasswordUtil.hashPassword("StrongPass123!");

        assertTrue(PasswordUtil.matches("StrongPass123!", hash));
    }

    @Test
    void rejectsIncorrectPasswords() {
        String hash = PasswordUtil.hashPassword("StrongPass123!");

        assertFalse(PasswordUtil.matches("WrongPass123!", hash));
    }

    @Test
    void rejectsMalformedHashInput() {
        assertFalse(PasswordUtil.matches("StrongPass123!", "not-a-valid-hash"));
        assertFalse(PasswordUtil.matches("StrongPass123!", "1$abcd$efgh"));
        assertFalse(PasswordUtil.matches("StrongPass123!", "1000001$abcd$efgh"));
    }
}
