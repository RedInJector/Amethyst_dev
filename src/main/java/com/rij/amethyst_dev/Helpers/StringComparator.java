package com.rij.amethyst_dev.Helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class StringComparator {
    public static boolean compareAPIKeys(String storedKey, String providedKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] storedBytes = md.digest(storedKey.getBytes());
            byte[] providedBytes = md.digest(providedKey.getBytes());

            return Arrays.equals(storedBytes, providedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
}
