package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An amount of money in euros. Immutable value object compared by value, not by identity.
 */
public final class Money {

    public static final Money ZERO = Money.euros(0);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money euros(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money euros(BigDecimal amount) {
        return new Money(amount);
    }

    public Money plus(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /** Removes a percentage from this amount, e.g. {@code euros(100).minusPercentage(10)} is 90 EUR. */
    public Money minusPercentage(int percent) {
        BigDecimal remainingFactor = BigDecimal.valueOf(100 - percent).divide(BigDecimal.valueOf(100));
        return new Money(amount.multiply(remainingFactor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " EUR";
    }
}
