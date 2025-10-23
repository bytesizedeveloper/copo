package org.acme.blockchain.common.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public record CoinModel(BigDecimal value) {

    private static final BigDecimal MINIMUM = BigDecimal.valueOf(0.00000001);

    private static final BigDecimal MAXIMUM = BigDecimal.valueOf(999999999.99999999);

    public static final DecimalFormat FORMAT = new DecimalFormat("0.00000000");

    public CoinModel {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid coin amount: " + value);
        }
    }

    public CoinModel add(CoinModel other) {
        BigDecimal value = this.value.add(other.value);
        return new CoinModel(value);
    }

    public CoinModel subtract(CoinModel other) {
        BigDecimal value = this.value.subtract(other.value);
        return new CoinModel(value);
    }

    public boolean isEqualTo(CoinModel other) {
        return this.value.compareTo(other.value) == 0;
    }

    public boolean isGreaterThanOrEqualTo(CoinModel other) {
        return this.value.compareTo(other.value) >= 0;
    }

    public boolean isLessThan(CoinModel other) {
        return this.value.compareTo(other.value) < 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return FORMAT.format(this.value);
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
