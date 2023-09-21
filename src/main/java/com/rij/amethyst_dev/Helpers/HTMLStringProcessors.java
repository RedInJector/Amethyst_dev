package com.rij.amethyst_dev.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLStringProcessors {
    public static String removeHtmlTags(String html) {
        // Regular expression pattern to match HTML tags and everything between them
        String pattern = "(?i)<(h\\d|a)[^>]*>.*?</\\1>";
        Pattern htmlTagPattern = Pattern.compile(pattern, Pattern.DOTALL);

        // Use Matcher to find and replace all HTML tags and their contents with an empty string
        Matcher matcher = htmlTagPattern.matcher(html);
        String plainText = matcher.replaceAll("");

        // Remove remaining HTML tags without their contents
        plainText = plainText.replaceAll("<[^>]*>", "");

        return plainText;
    }

    public static String extractTextAroundWord(String text, String targetWord) {
        int targetIndex = text.toLowerCase().indexOf(targetWord.toLowerCase());

        // If the target word is not found or the total length is less than 80 characters, return the original text
        if(text.length() < 80)
            return text;

        if (targetIndex == -1) {
            return null;
        }

        // Calculate the start and end indices for the extracted substring
        int startIndex = Math.max(0, targetIndex - 40);
        int endIndex = Math.min(text.length(), targetIndex + targetWord.length() + 40);

        // Extend the start index to include any characters until the previous space
        while (startIndex > 0 && !Character.isWhitespace(text.charAt(startIndex - 1))) {
            startIndex--;
        }

        // Extend the end index to include any characters until the next space
        while (endIndex < text.length() && !Character.isWhitespace(text.charAt(endIndex))) {
            endIndex++;
        }

        // Add "..." at the start if the extracted substring is not at the beginning of the original text
        StringBuilder result = new StringBuilder();
        if (startIndex > 0) {
            result.append("...");
        }

        // Extract the substring with characters on each side of the target word
        result.append(text.substring(startIndex, endIndex));

        // Add "..." at the end if the extracted substring is not at the end of the original text
        if (endIndex < text.length()) {
            result.append("...");
        }

        return result.toString();
    }


    public static boolean hasAtLeastOneNonSpaceCharacter(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
