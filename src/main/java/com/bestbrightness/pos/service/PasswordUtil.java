package com.bestbrightness.pos.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.security.spec.InvalidKeySpecException;
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
        return hashPassword(password == null ? null : password.toCharArray());
    }

    public static String hashPassword(char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is required.");
        }
        try {
            byte[] salt = new byte[SALT_LENGTH];
            SECURE_RANDOM.nextBytes(salt);
            byte[] hash = deriveKey(password, salt, ITERATIONS, KEY_LENGTH);
            return ITERATIONS
                    + "$"
                    + HexFormat.of().formatHex(salt)
                    + "$"
                    + HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("Password hashing is not available.", exception);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public static boolean matches(String rawPassword, String passwordHash) {
        return matches(rawPassword == null ? null : rawPassword.toCharArray(), passwordHash);
    }

    public static boolean matches(char[] rawPassword, String passwordHash) {
        if (passwordHash == null || rawPassword == null) {
            return false;
        }
        try {
            String[] parts = passwordHash.split("\\$", -1);
            if (parts.length != 3) {
                return false;
            }
            int iterations = Integer.parseInt(parts[0]);
            if (iterations <= 0 || iterations > MAX_ITERATIONS) {
                return false;
            }
            byte[] salt = HexFormat.of().parseHex(parts[1]);
            byte[] expectedHash = HexFormat.of().parseHex(parts[2]);
            if (salt.length != SALT_LENGTH || expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }
            byte[] actualHash = deriveKey(rawPassword, salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | RuntimeException exception) {
            return false;
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
