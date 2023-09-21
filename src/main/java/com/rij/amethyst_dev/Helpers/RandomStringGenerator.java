package com.rij.amethyst_dev.Helpers;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RandomStringGenerator {
    private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer");
        }

        char[] randomChars = new char[length];

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(symbols.length());
            randomChars[i] = symbols.charAt(index);
        }

        return new String(randomChars);
    }

    public static String generateAccessKey(){

        return generate(64);
    }
}
