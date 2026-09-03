// training-examples/chapter-11 — Démonstration 1 : vérification excessive des interactions.
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcessiveInteractionVerificationTest {

    @Test
    void evaluate_price_delegates_to_pricing_policy_and_returns_95_euros() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        SeatSelection chosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);
        Order order = new Order(OrderId.newId(), premiumCustomer, trajet, List.of(adult), List.of(chosenSeat));

        // Un développeur consciencieux vérifie "en plus" que le calcul passe bien par
        // PricingPolicy, pensant renforcer le test. C'est exactement ce détail d'implémentation
        // qui le rend fragile : voir PricingPolicy, introduite par le commit 07.
        PricingPolicy pricingPolicy = new PricingPolicy();
        assertEquals(Money.euros(90), pricingPolicy.transportPriceFor(premiumCustomer, trajet));
        assertEquals(Money.euros(5), pricingPolicy.seatFeeFor(chosenSeat));

        assertEquals(Money.euros(95), order.evaluatePrice());
    }
}
