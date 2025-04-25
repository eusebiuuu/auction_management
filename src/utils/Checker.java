package utils;

import java.util.regex.Pattern;

public class Checker {

    /**
     * Checks if an integer is within a specified range (inclusive)
     * @param value The integer to check
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return true if the value is within range
     * @throws IllegalArgumentException if min > max
     */
    public static boolean isInRange(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        return value >= min && value <= max;
    }

    /**
     * Checks if a number is positive
     * @param number The number to check
     * @return true if the number is positive
     * @throws IllegalArgumentException if number is null
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }

        if (number instanceof Integer) {
            return number.intValue() > 0;
        } else if (number instanceof Double) {
            return number.doubleValue() > 0;
        } else if (number instanceof Long) {
            return number.longValue() > 0;
        } else if (number instanceof Float) {
            return number.floatValue() > 0;
        }
        return false;
    }

    /**
     * Checks if a string is not null and not empty
     * @param str The string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Checks if a string matches a given regex pattern
     * @param str The string to check
     * @param regex The regular expression pattern
     * @return true if the string matches the pattern
     * @throws IllegalArgumentException if str or regex is null
     * @throws java.util.regex.PatternSyntaxException if regex is invalid
     */
    public static boolean matchesPattern(String str, String regex) {
        if (str == null || regex == null) {
            throw new IllegalArgumentException("Neither string nor regex can be null");
        }
        return Pattern.matches(regex, str);
    }
}