package org.acme.blockchain.common.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Coin(BigDecimal value) {

    public static final int SCALE = 8;

    public static final BigDecimal MINIMUM = BigDecimal.valueOf(0.00000001);

    public static final BigDecimal MAXIMUM = BigDecimal.valueOf(999999999.99999999);

    public static final Coin ZERO = new Coin(BigDecimal.ZERO);

    public Coin {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid COPO amount: " + value);
        }
        value = value.setScale(SCALE, RoundingMode.DOWN);
    }

    public Coin add(Coin other) {
        BigDecimal value = this.value.add(other.value);
        return new Coin(value);
    }

    public Coin subtract(Coin other) {
        BigDecimal value = this.value.subtract(other.value);
        return new Coin(value);
    }

    public boolean isEqualTo(Coin other) {
        return this.value.compareTo(other.value) == 0;
    }

    public boolean isGreaterThanOrEqualTo(Coin other) {
        return this.value.compareTo(other.value) >= 0;
    }

    public boolean isLessThan(Coin other) {
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isZero() {
        return this.equals(ZERO);
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isValid(BigDecimal value) {
        return value != null
                && ((isZero(value))
                || (isGreaterThanOrEqualToMinimum(value)
                && isLessThanOrEqualToMaximum(value)));
    }

    public boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isGreaterThanOrEqualToMinimum(BigDecimal value) {
        return value.compareTo(MINIMUM) >= 0;
    }

    private boolean isLessThanOrEqualToMaximum(BigDecimal value) {
        return value.compareTo(MAXIMUM) <= 0;
    }
}
