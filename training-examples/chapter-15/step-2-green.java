// training-examples/chapter-15 — GREEN : implémentation minimale, pas encore élégante.
package domain;

class PricingPolicyGreenStep {

    private static final int PREMIUM_DISCOUNT_PERCENT = 10;
    private static final int SENIOR_AGE_THRESHOLD = 65;

    Money transportPriceFor(Customer customer, Trajet trajet, Passenger passenger) {
        // Condition ajoutée telle quelle pour faire passer le test au plus vite.
        if (customer.isPremium() || passenger.age() >= SENIOR_AGE_THRESHOLD) {
            return trajet.basePrice().minusPercentage(PREMIUM_DISCOUNT_PERCENT);
        }
        return trajet.basePrice();
    }
}
