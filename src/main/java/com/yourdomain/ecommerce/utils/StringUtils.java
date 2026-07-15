package com.yourdomain.ecommerce.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGE_DASHES = Pattern.compile("(^-+)|(-+$)");

    private StringUtils() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static String slugify(String input) {
        if (input == null) {
            return null;
        }
        String noWhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGE_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ROOT);
    }

    public static String maskEmail(String email) {
        if (isBlank(email) || !email.contains("@")) {
            return email;
        }
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);
        if (local.length() <= 2) {
            return "*".repeat(local.length()) + domain;
        }
        return local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1) + domain;
    }
}
