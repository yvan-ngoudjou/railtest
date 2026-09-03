package domain;

import java.util.List;

/**
 * The purchase being made by a {@link Customer} for one or more {@link Passenger}s on a
 * {@link Trajet}.
 */
public final class Order {

    private static final int PREMIUM_DISCOUNT_PERCENT = 10;

    private final OrderId id;
    private final Customer customer;
    private final Trajet trajet;
    private final List<Passenger> passengers;
    private final List<SeatSelection> seatSelections;

    public Order(
            OrderId id,
            Customer customer,
            Trajet trajet,
            List<Passenger> passengers,
            List<SeatSelection> seatSelections
    ) {
        if (passengers.size() != seatSelections.size()) {
            throw new IllegalArgumentException("Each passenger must have exactly one seat selection");
        }
        this.id = id;
        this.customer = customer;
        this.trajet = trajet;
        this.passengers = List.copyOf(passengers);
        this.seatSelections = List.copyOf(seatSelections);
    }

    /** The total amount the customer must pay for this order. */
    public Money evaluatePrice() {
        return passengers.stream()
                .map(this::priceFor)
                .reduce(Money.ZERO, Money::plus);
    }

    private Money priceFor(Passenger passenger) {
        if (passenger.isChild()) {
            return Money.ZERO;
        }
        Money transportPrice = customer.isPremium()
                ? trajet.basePrice().minusPercentage(PREMIUM_DISCOUNT_PERCENT)
                : trajet.basePrice();
        return transportPrice.plus(seatSelectionFor(passenger).fee());
    }

    private SeatSelection seatSelectionFor(Passenger passenger) {
        return seatSelections.stream()
                .filter(selection -> selection.passengerId().equals(passenger.id()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No seat selection for passenger " + passenger.id()));
    }

    public OrderId id() {
        return id;
    }

    public Customer customer() {
        return customer;
    }

    public Trajet trajet() {
        return trajet;
    }

    public List<Passenger> passengers() {
        return passengers;
    }

    public List<SeatSelection> seatSelections() {
        return seatSelections;
    }
}
