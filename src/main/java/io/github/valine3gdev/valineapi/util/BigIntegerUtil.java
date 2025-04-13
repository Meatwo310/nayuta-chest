package io.github.valine3gdev.valineapi.util;

import java.math.BigInteger;

public class BigIntegerUtil {
    public static final BigInteger HUNDRED = BigInteger.valueOf(100);
    public static final BigInteger NOVEMDECILLION = BigInteger.TEN.pow(60);

    /**
     * Converts a BigInteger to an int, clamping to {@link Integer#MAX_VALUE}.
     * @see #asInt(BigInteger, int)
     */
    public static int asInt(BigInteger value) {
        return asInt(value, Integer.MAX_VALUE);
    }

    /**
     * Converts a BigInteger to an int, clamping to the maximum value.
     * Returns {@code max} even if {@code value} cannot be converted to an int.
     * @param value BigInteger to convert
     * @param max Maximum value to return
     * @return The converted int value
     */
    public static int asInt(BigInteger value, int max) {
        if (value.compareTo(BigInteger.valueOf(max)) > 0) return max;
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            return max;
        }
    }

    /**
     * Converts a BigInteger to an int if it fits in the integer range.
     * If the value is too large, executes the provided consumer and returns a default value.
     * @param value BigInteger to convert
     * @param defaultValue Value to return when conversion fails
     * @param tooLargeHandler Consumer that gets executed when the value is too large
     * @return The converted int value or the default value
     */
    public static int asInt(BigInteger value, int defaultValue, Runnable tooLargeHandler) {
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            tooLargeHandler.run();
            return defaultValue;
        }
    }
}
