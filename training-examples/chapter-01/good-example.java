// training-examples/chapter-01 — extrait réel du projet, non modifié.
// Source : src/test/java/domain/OrderPricingTest.java
package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PourquoiTesterExampleTest {

    @Test
    void premium_customer_gets_10_percent_discount_on_transport() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                java.time.LocalDateTime.of(2026, 6, 1, 8, 0),
                java.time.LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        SeatSelection automaticSeat = new SeatSelection(adult.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), premiumCustomer, trajet, List.of(adult), List.of(automaticSeat));

        assertEquals(Money.euros(90), order.evaluatePrice());
    }
}
