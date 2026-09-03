// training-examples/chapter-02 — BON EXEMPLE.
// Extrait réel : src/test/java/domain/OrderPricingTest.java
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeatFeeBehaviorTest {

    @Test
    void choosing_a_seat_adds_a_five_euro_fee() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer customer = new Customer(CustomerId.newId(), "bob@example.com", false);
        Passenger adult = new Passenger(PassengerId.newId(), "Bob", 30);
        SeatSelection chosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);

        Order order = new Order(OrderId.newId(), customer, trajet, List.of(adult), List.of(chosenSeat));

        assertEquals(Money.euros(105), order.evaluatePrice());
    }
}
