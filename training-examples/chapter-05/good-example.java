// training-examples/chapter-05 — même test que OrderPricingTest, annoté.
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChildFareAnatomyTest {

    @Test
    void child_under_10_years_old_travels_for_free() {
        // --- Fixture : les objets nécessaires pour rendre la situation réaliste ---
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer customer = new Customer(CustomerId.newId(), "parent@example.com", false);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 9);
        SeatSelection chosenSeat = new SeatSelection(child.id(), SeatId.newId(), true);

        // --- Arrange : construire le SUT (Order) dans l'état voulu ---
        Order order = new Order(OrderId.newId(), customer, trajet, List.of(child), List.of(chosenSeat));

        // --- Act : une seule action, celle dont on veut prouver le comportement ---
        Money price = order.evaluatePrice();

        // --- Assert : l'outcome observable, rien d'autre ---
        assertEquals(Money.ZERO, price);
    }
}
