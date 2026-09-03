// training-examples/chapter-07 — BON EXEMPLE : seul l'outcome observable compte.
package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObservableOutcomeTest {

    @Test
    void buying_for_a_premium_adult_with_chosen_seat_and_a_free_child_costs_95_euros() {
        Trajet trajet = new Trajet(
                TrajetId.newId(), new Station("Paris"), new Station("Lyon"),
                LocalDateTime.of(2026, 6, 1, 8, 0), LocalDateTime.of(2026, 6, 1, 10, 0),
                Money.euros(100), TrainId.newId()
        );
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 8);
        SeatSelection adultChosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);
        SeatSelection childAutomaticSeat = new SeatSelection(child.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), premiumCustomer, trajet,
                List.of(adult, child), List.of(adultChosenSeat, childAutomaticSeat));

        assertEquals(Money.euros(95), order.evaluatePrice());
    }
}
