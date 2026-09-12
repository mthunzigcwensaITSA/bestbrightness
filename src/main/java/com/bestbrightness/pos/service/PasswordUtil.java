package com.bestbrightness.pos.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final int ITERATIONS = 65_536;
    private static final int MAX_ITERATIONS = 1_000_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);
            byte[] hash = deriveKey(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return ITERATIONS
                    + "$"
                    + HexFormat.of().formatHex(salt)
                    + "$"
                    + HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Password hashing is not available.", exception);
        }
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        if (passwordHash == null || rawPassword == null) {
            return false;
        }
        try {
            String[] parts = passwordHash.split("\\$");
            if (parts.length != 3) {
                return false;
            }
            int iterations = Integer.parseInt(parts[0]);
            if (iterations < ITERATIONS || iterations > MAX_ITERATIONS) {
                return false;
            }
            byte[] salt = HexFormat.of().parseHex(parts[1]);
            byte[] expectedHash = HexFormat.of().parseHex(parts[2]);
            byte[] actualHash = deriveKey(rawPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterations, int keyLength) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception exception) {
            throw new IllegalStateException("PBKDF2WithHmacSHA256 is not available.", exception);
        } finally {
            spec.clearPassword();
        }
    }
}
