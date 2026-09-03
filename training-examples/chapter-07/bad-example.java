// training-examples/chapter-07 — MAUVAIS EXEMPLE : le test réimplémente l'algorithme du SUT.
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReimplementsAlgorithmTest {

    @Test
    void testEvaluatePriceForPremiumAdultAndChild() {
        Money basePrice = Money.euros(100);
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                basePrice, TrainId.newId()
        );
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 8);
        SeatSelection adultChosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);
        SeatSelection childAutomaticSeat = new SeatSelection(child.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), premiumCustomer, trajet,
                List.of(adult, child), List.of(adultChosenSeat, childAutomaticSeat));

        // Le test recalcule lui-même ce que fait PricingPolicy, au lieu d'observer le résultat.
        PricingPolicy pricingPolicy = new PricingPolicy();
        Money expectedAdultPrice = pricingPolicy.transportPriceFor(premiumCustomer, trajet)
                .plus(pricingPolicy.seatFeeFor(adultChosenSeat));
        Money expectedChildPrice = Money.ZERO; // dupliqué depuis Order.priceFor()
        Money expectedTotal = expectedAdultPrice.plus(expectedChildPrice);

        assertEquals(expectedTotal, order.evaluatePrice());
    }
}
