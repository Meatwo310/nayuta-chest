package io.github.meatwo310.nayutachest.util;

import java.math.BigInteger;

public class BigIntegerUtil {
    public static int asIntOr(BigInteger value) {
        return asIntOr(value, Integer.MAX_VALUE);
    }

    public static int asIntOr(BigInteger value, int max) {
        if (value.compareTo(BigInteger.valueOf(max)) > 0) return max;
        try {
            return value.intValueExact();
        } catch (ArithmeticException e) {
            return max;
        }
    }
}
