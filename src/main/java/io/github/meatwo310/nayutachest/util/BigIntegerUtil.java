package io.github.meatwo310.nayutachest.util;

import java.math.BigInteger;

public class BigIntegerUtil {
    /**
     * Converts a BigInteger to an int, clamping to {@link Integer#MAX_VALUE}.
     * @see #asIntOr(BigInteger, int)
     */
    public static int asIntOr(BigInteger value) {
        return asIntOr(value, Integer.MAX_VALUE);
    }

    /**
     * Converts a BigInteger to an int, clamping to the maximum value.
     * Returns {@code max} even if {@code value} cannot be converted to an int.
     * @param value BigInteger to convert
     * @param max Maximum value to return
     * @return The converted int value
     */
    public static int asIntOr(BigInteger value, int max) {
        if (value.compareTo(BigInteger.valueOf(max)) > 0) return max;
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            return max;
        }
    }
}
