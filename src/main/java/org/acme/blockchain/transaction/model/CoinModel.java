package org.acme.blockchain.transaction.model;

import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@EqualsAndHashCode
public class CoinModel {

    public static final BigDecimal MINIMUM = BigDecimal.valueOf(0.00000001);

    public static final BigDecimal MAXIMUM = BigDecimal.valueOf(999999999.99999999);

    public static final int SCALE = 8;

    public static final DecimalFormat FORMAT = new DecimalFormat("0.00000000", DecimalFormatSymbols.getInstance(Locale.UK));

    @EqualsAndHashCode.Exclude
    private final BigDecimal value;

    public CoinModel(BigDecimal value) {
        this.value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal value() {
        return this.value;
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

    public boolean isLessThanMinimum() {
        return MINIMUM.compareTo(this.value) > 0;
    }

    public boolean isGreaterThanMaximum() {
        return MAXIMUM.compareTo(this.value) < 0;
    }

    public boolean isZero() {
        return BigDecimal.ZERO.compareTo(this.value) == 0;
    }

    public boolean isPositive() {
        return BigDecimal.ZERO.compareTo(this.value) < 0;
    }

    @Override
    public String toString() {
        return FORMAT.format(this.value);
    }

    @EqualsAndHashCode.Include
    private BigDecimal getValueForEquals() {
        return this.value != null ? this.value.stripTrailingZeros() : null;
    }
}
