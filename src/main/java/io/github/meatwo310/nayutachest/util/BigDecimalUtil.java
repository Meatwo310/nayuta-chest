package io.github.meatwo310.nayutachest.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BigDecimalUtil {
    public static final BigDecimal HUNDRED = new BigDecimal(100);

    public static BigDecimal getRate(BigInteger amount, BigInteger max, int scale) {
        return getRate(new BigDecimal(amount), new BigDecimal(max), scale);
    }

    public static BigDecimal getRate(BigDecimal amount, BigDecimal max, int scale) {
        return amount.divide(max, scale, RoundingMode.DOWN);
    }
}
