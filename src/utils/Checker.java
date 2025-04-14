package utils;

import java.util.regex.Pattern;

public class Checker {

    /**
     * Checks if an integer is within a specified range (inclusive)
     * @param value The integer to check
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return true if the value is within range, false otherwise
     */
    public static boolean isInRange(int value, int min, int max) {
        try {
            return value >= min && value <= max;
        } catch (Exception e) {
            handleError("Range check failed", e);
            return false;
        }
    }

    /**
     * Checks if a number is positive
     * @param number The number to check
     * @return true if the number is positive, false otherwise
     */
    public static boolean isPositive(Number number) {
        try {
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
        } catch (Exception e) {
            handleError("Positive check failed", e);
            return false;
        }
    }

    /**
     * Checks if a string is not null and not empty
     * @param str The string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotNullOrEmpty(String str) {
        try {
            return str != null && !str.trim().isEmpty();
        } catch (Exception e) {
            handleError("Null or empty check failed", e);
            return false;
        }
    }

    /**
     * Checks if a string matches a given regex pattern
     * @param str The string to check
     * @param regex The regular expression pattern
     * @return true if the string matches the pattern
     */
    public static boolean matchesPattern(String str, String regex) {
        try {
            return str != null && Pattern.matches(regex, str);
        } catch (Exception e) {
            handleError("Pattern matching failed", e);
            return false;
        }
    }

    /**
     * Checks if a string can be parsed to a number
     * @param str The string to check
     * @return true if the string can be parsed to a number
     */
    public static boolean isNumeric(String str) {
        try {
            if (str == null) {
                return false;
            }
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (Exception e) {
            handleError("Numeric check failed", e);
            return false;
        }
    }

    /**
     * Handles errors by logging them (in a real application, you might want to use a logger)
     * @param message The error message
     * @param e The exception
     */
    private static void handleError(String message, Exception e) {
        // In a real application, use a proper logging framework
        System.err.println(message + ": " + e.getMessage());
        // e.printStackTrace(); // Uncomment for debugging
    }
}
