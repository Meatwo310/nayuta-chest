package io.github.meatwo310.nayutachest.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

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


    /**
     * Converts a BigInteger to a scientific notation string with the specified precision.
     * @param value The BigInteger value to convert
     * @param precision The number of significant digits to include in the result
     * @return The scientific notation string
     */
    public static String toScientificNotation(BigInteger value, int precision) {
        BigDecimal bigDecimal = new BigDecimal(value);
        BigDecimal scaled = bigDecimal.setScale(precision - bigDecimal.precision() + bigDecimal.scale(), RoundingMode.FLOOR);
        return scaled
                .toString()
                .replaceFirst("\\.0+$", "")
                .replaceFirst("^0E-.+$", "0");
    }


}
