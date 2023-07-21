package com.rij.amethyst_dev.Helpers;

public class MinecraftNameValidator {
    public static boolean check(String input){
        if (input == null || input.isEmpty()) {
            return false;
        }

        // Check for length greater than 20
        if (input.length() > 20 || input.length() < 3) {
            return false;
        }

        // Check for non-alphabetic characters or spaces
        String pattern = "^[A-Za-z0-9_]+$";
        return input.matches(pattern);
    }
}
