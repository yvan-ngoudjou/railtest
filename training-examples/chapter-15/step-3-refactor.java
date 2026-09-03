// training-examples/chapter-15 — REFACTOR : même comportement, condition nommée explicitement.
// Le test de l'étape RED (step-1-red.java) n'est pas modifié et reste vert.
package domain;

class PricingPolicyRefactored {

    private static final int PREMIUM_DISCOUNT_PERCENT = 10;
    private static final int SENIOR_AGE_THRESHOLD = 65;

    Money transportPriceFor(Customer customer, Trajet trajet, Passenger passenger) {
        return isEligibleForTransportDiscount(customer, passenger)
                ? trajet.basePrice().minusPercentage(PREMIUM_DISCOUNT_PERCENT)
                : trajet.basePrice();
    }

    private boolean isEligibleForTransportDiscount(Customer customer, Passenger passenger) {
        return customer.isPremium() || isSenior(passenger);
    }

    private boolean isSenior(Passenger passenger) {
        return passenger.age() >= SENIOR_AGE_THRESHOLD;
    }
}
