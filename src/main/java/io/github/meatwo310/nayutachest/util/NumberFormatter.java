package io.github.meatwo310.nayutachest.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.BiFunction;

public class NumberFormatter {
    protected final BigInteger value;

    // short scale units
    public static final int SHORT_SCALE_UNITS_INTERVAL = 3;
    public static final String[] SHORT_SCALE_UNITS = {
            "",
            "thousand",
            "million",
            "billion",
            "trillion",
            "quadrillion",
            "quintillion",
            "sextillion",
            "septillion",
            "octillion",
            "nonillion",
            "decillion",
            "undecillion",
            "duodecillion",
            "tredecillion",
            "quattuordecillion",
            "quindecillion",
            "sexdecillion",
            "septendecillion",
            "octodecillion",
            "novemdecillion",
            "vigintillion",
            "unvigintillion",
    };

    // short scale units but shorter
    public static final int SHORTER_SCALE_UNITS_INTERVAL = SHORT_SCALE_UNITS_INTERVAL;
    public static final String[] SHORTER_SCALE_UNITS = {
            "",
            "k",
            "M",
            "B",
            "T",
            "Qa",
            "Qi",
            "Sx",
            "Sp",
            "Oc",
            "No",
            "Dc",
            "Ud",
            "Dd",
            "Td",
            "Qad",
            "Qid",
            "Sxd",
            "Spd",
            "Od",
            "Nd",
            "Vg",
            "Uv",
    };

    // constructor
    public NumberFormatter(BigInteger value) {
        this.value = value;
    }

    public NumberFormatter(long value) {
        this(BigInteger.valueOf(value));
    }

    // public conversion methods
    public String to(Format numberType, int precision) {
        return switch (numberType) {
            case SCIENTIFIC -> this.toScientificNotation(precision);
            case KANSUJI -> this.toKansuji(precision);
            case NUMERIC_KANSUJI -> this.toNumericKansuji(precision);
            case METRIC -> this.toMetric(precision);
            case SHORT_SCALE -> this.toShortScale(precision);
            case SHORTER_SCALE -> this.toShorterScale(precision);
        };
    }

    /**
     * Formats the value using scientific notation.
     * @param precision The number of decimal places to display.
     * @return The formatted string
     */
    public String toScientificNotation(int precision) {
        return trimZeros(scaleWithNotationE(this.value, precision));
    }

    public String toKansuji(int precision) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String toNumericKansuji(int precision) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String toMetric(int precision) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String toShortScale(int precision) {
        return this.format(this.value, SHORT_SCALE_UNITS, SHORT_SCALE_UNITS_INTERVAL, precision, (bigDecimal, unitValue) ->
                trimMoreZeros(bigDecimal) + " "
        );
    }

    public String toShorterScale(int precision) {
        return this.format(this.value, SHORTER_SCALE_UNITS, SHORTER_SCALE_UNITS_INTERVAL, precision, (bigDecimal, unitValue) ->
                trimMoreZeros(bigDecimal) + " "
        );
    }

    // private helper methods
    /**
     * Fallback to another formatter when formatting fails (e.g., value is too large).
     * @param precision The number of decimal places to display
     * @return The formatted string
     */
    private String fallback(int precision) {
        return this.toScientificNotation(precision);
    }

    private static String trimZeros(BigDecimal value) {
        return value
                .toString()
                .replaceFirst("\\.0+$", "")
                .replaceFirst("^0E-.+$", "0");
    }

    private static String trimMoreZeros(BigDecimal value) {
        return value
                .toString()
                .replaceFirst("\\.?0+$", "")
                .replaceFirst("^0E-.+$", "0");
    }

    private static BigDecimal scaleWithNotationE(BigInteger value, int precision) {
        return scaleWithNotationE(new BigDecimal(value), precision);
    }

    private static BigDecimal scaleWithNotationE(BigDecimal value, int precision) {
        return value.setScale(precision - value.precision() + value.scale(), RoundingMode.FLOOR);
    }

    private String format(BigInteger bigInteger, String[] units, int unitsInterval, int precision, BiFunction<BigDecimal, UnitValue, String> formatter) {
        int digits = getDigits(bigInteger);
        if (digits <= unitsInterval) {
            return bigInteger.toString();
        }
        Optional<UnitValue> unitValue = getUnit(units, unitsInterval, digits);
        return unitValue.map(unit -> {
            BigDecimal bigDecimal = new BigDecimal(bigInteger)
                    .movePointLeft(unit.unitIndex * unitsInterval)
                    .setScale(precision, RoundingMode.FLOOR);
            return formatter.apply(bigDecimal, unit) + unit.unit;
        }).orElse(fallback(precision));
    }

    private static int getDigits(BigInteger bigInt) {
        return bigInt.toString().length();
    }

    private static Optional<UnitValue> getUnit(String[] units, int unitsInterval, int digits) {
        int unitIndex = (digits - 1) / unitsInterval;
        if (unitIndex >= units.length) {
            return Optional.empty();
        }
        return Optional.of(new UnitValue(unitIndex, units[unitIndex]));
    }

    private record UnitValue(int unitIndex, String unit) {
        public UnitValue {
            if (unitIndex < 0) {
                throw new IllegalArgumentException("Unit index must be non-negative, got " + unitIndex);
            }
        }
    }

    // format enum
    public enum Format {
        SCIENTIFIC,
        KANSUJI,
        NUMERIC_KANSUJI,
        METRIC,
        SHORT_SCALE,
        SHORTER_SCALE,
    }
}
