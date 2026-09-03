// training-examples/chapter-15 — RED : ce test échoue contre le code du commit 09.
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeniorDiscountRedTest {

    @Test
    void senior_passenger_gets_10_percent_discount_on_transport() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer regularCustomer = new Customer(CustomerId.newId(), "bob@example.com", false);
        Passenger senior = new Passenger(PassengerId.newId(), "Grandpa", 70);
        SeatSelection automaticSeat = new SeatSelection(senior.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), regularCustomer, trajet, List.of(senior), List.of(automaticSeat));

        // Échoue : expected <90.00 EUR> but was <100.00 EUR> — l'âge n'influence pas encore le prix.
        assertEquals(Money.euros(90), order.evaluatePrice());
    }
}
