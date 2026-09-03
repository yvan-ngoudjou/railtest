package domain;

/**
 * Computes the two price components a passenger may owe: the transport price (discounted for
 * premium customers) and the seat selection fee (never discounted). Extracted out of
 * {@link Order} as an internal refactoring: it does not change what {@link Order#evaluatePrice()}
 * returns.
 */
class PricingPolicy {

    private static final int PREMIUM_DISCOUNT_PERCENT = 10;

    Money transportPriceFor(Customer customer, Trajet trajet) {
        return customer.isPremium()
                ? trajet.basePrice().minusPercentage(PREMIUM_DISCOUNT_PERCENT)
                : trajet.basePrice();
    }

    Money seatFeeFor(SeatSelection seatSelection) {
        return seatSelection.fee();
    }
}
