package domain;

/**
 * The seat assigned to a passenger for an order, and whether the customer paid to choose it.
 * An automatically assigned seat carries no fee; an explicitly chosen one does.
 */
public record SeatSelection(PassengerId passengerId, SeatId seatId, boolean chosenByCustomer) {

    private static final Money SEAT_SELECTION_FEE = Money.euros(5);

    public Money fee() {
        return chosenByCustomer ? SEAT_SELECTION_FEE : Money.ZERO;
    }
}
