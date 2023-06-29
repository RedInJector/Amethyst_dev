package com.rij.amethyst_dev.Helpers;

import java.util.Random;

public class RandomStringGenerator {
    private static final String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static String generate(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(symbols.length());
            char randomChar = symbols.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
