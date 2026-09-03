package domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reference behavioral tests for {@link Order#evaluatePrice()}.
 *
 * These tests describe pricing rules from the customer's point of view: given a customer,
 * some passengers and their seat selections, what is the total price? They know nothing about
 * how the computation is implemented internally, so the pricing logic can be refactored freely
 * as long as these outcomes hold.
 */
class OrderPricingTest {

    private static final Trajet TRAJET = new Trajet(
            TrajetId.newId(),
            new Station("Paris"),
            new Station("Lyon"),
            LocalDateTime.of(2026, 6, 1, 8, 0),
            LocalDateTime.of(2026, 6, 1, 10, 0),
            Money.euros(100),
            TrainId.newId()
    );

    @Test
    void premium_customer_gets_10_percent_discount_on_transport() {
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        SeatSelection automaticSeat = new SeatSelection(adult.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), premiumCustomer, TRAJET, List.of(adult), List.of(automaticSeat));

        assertEquals(Money.euros(90), order.evaluatePrice());
    }

    @Test
    void regular_customer_pays_full_transport_price() {
        Customer regularCustomer = new Customer(CustomerId.newId(), "bob@example.com", false);
        Passenger adult = new Passenger(PassengerId.newId(), "Bob", 30);
        SeatSelection automaticSeat = new SeatSelection(adult.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), regularCustomer, TRAJET, List.of(adult), List.of(automaticSeat));

        assertEquals(Money.euros(100), order.evaluatePrice());
    }

    @Test
    void child_under_10_years_old_travels_for_free() {
        Customer customer = new Customer(CustomerId.newId(), "parent@example.com", false);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 9);
        SeatSelection chosenSeat = new SeatSelection(child.id(), SeatId.newId(), true);

        Order order = new Order(OrderId.newId(), customer, TRAJET, List.of(child), List.of(chosenSeat));

        assertEquals(Money.ZERO, order.evaluatePrice());
    }

    @Test
    void ten_year_old_passenger_pays_the_regular_fare() {
        Customer customer = new Customer(CustomerId.newId(), "parent@example.com", false);
        Passenger tenYearsOld = new Passenger(PassengerId.newId(), "Mia", 10);
        SeatSelection automaticSeat = new SeatSelection(tenYearsOld.id(), SeatId.newId(), false);

        Order order = new Order(OrderId.newId(), customer, TRAJET, List.of(tenYearsOld), List.of(automaticSeat));

        assertEquals(Money.euros(100), order.evaluatePrice());
    }

    @Test
    void choosing_a_seat_adds_a_five_euro_fee() {
        Customer customer = new Customer(CustomerId.newId(), "bob@example.com", false);
        Passenger adult = new Passenger(PassengerId.newId(), "Bob", 30);
        SeatSelection chosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);

        Order order = new Order(OrderId.newId(), customer, TRAJET, List.of(adult), List.of(chosenSeat));

        assertEquals(Money.euros(105), order.evaluatePrice());
    }

    @Test
    void seat_selection_fee_is_not_discounted_for_premium_customers() {
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        SeatSelection chosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);

        Order order = new Order(OrderId.newId(), premiumCustomer, TRAJET, List.of(adult), List.of(chosenSeat));

        // 100 - 10% premium discount = 90, + 5 for the chosen seat (not discounted) = 95
        assertEquals(Money.euros(95), order.evaluatePrice());
    }

    @Test
    void child_does_not_pay_the_seat_selection_fee_even_when_a_seat_is_chosen() {
        Customer customer = new Customer(CustomerId.newId(), "parent@example.com", false);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 8);
        SeatSelection chosenSeat = new SeatSelection(child.id(), SeatId.newId(), true);

        Order order = new Order(OrderId.newId(), customer, TRAJET, List.of(child), List.of(chosenSeat));

        assertEquals(Money.ZERO, order.evaluatePrice());
    }

    @Test
    void total_price_is_the_sum_of_every_passenger() {
        Customer premiumCustomer = new Customer(CustomerId.newId(), "alice@example.com", true);
        Passenger adult = new Passenger(PassengerId.newId(), "Alice", 30);
        Passenger child = new Passenger(PassengerId.newId(), "Leo", 8);
        SeatSelection adultChosenSeat = new SeatSelection(adult.id(), SeatId.newId(), true);
        SeatSelection childAutomaticSeat = new SeatSelection(child.id(), SeatId.newId(), false);

        Order order = new Order(
                OrderId.newId(), premiumCustomer, TRAJET,
                List.of(adult, child), List.of(adultChosenSeat, childAutomaticSeat)
        );

        // adult: 90 + 5, child: free
        assertEquals(Money.euros(95), order.evaluatePrice());
    }
}
