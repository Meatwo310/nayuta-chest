package io.github.meatwo310.nayutachest.util;

import java.math.BigInteger;

/**
 * Represents a large value as the base int value and the amount to shift to the left.
 *
 * @param base  The base int value
 * @param shift The amount to shift to the left
 */
public record IntShift(int base, int shift) {
    /**
     * Creates a new IntShift with the specified base and shift.
     *
     * @throws IllegalArgumentException If the base or shift is negative
     * @see IntShift
     */
    public IntShift {
        if (base < 0) throw new IllegalArgumentException("Base must be non-negative, got " + base);
        if (shift < 0) throw new IllegalArgumentException("Shift must be non-negative, got " + shift);
    }

    /**
     * Converts a BigInteger to an IntShift.
     * The upper 31 bits are preserved.
     * @see IntShift#fromBigInteger(BigInteger, int)
     */
    public static IntShift fromBigInteger(BigInteger value) {
        return fromBigInteger(value, Integer.SIZE - 1);
    }

    /**
     * Converts a BigInteger to an IntShift.
     * At least the upper {@code maxBits} bits are preserved.
     * For larger values, the lower bits are discarded,
     * resulting in a loss of precision.
     *
     * @param value BigInteger to convert
     * @return The IntShift representation
     * @throws IllegalArgumentException If the value is negative
     */
    public static IntShift fromBigInteger(BigInteger value, int maxBits) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Value must be non-negative, got " + value);
        }

        int bits = value.bitLength();
        int baseValueBits = Math.min(bits, maxBits);
        int toShift = bits - baseValueBits;
        int base = value.shiftRight(toShift).intValueExact();

        return new IntShift(base, toShift);
    }

    /**
     * Converts the IntShift to a BigInteger.
     *
     * @return The BigInteger representation
     */
    public BigInteger toBigInteger() {
        return BigInteger.valueOf(base).shiftLeft(shift);
    }
}
