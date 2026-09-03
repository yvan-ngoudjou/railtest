package application;

import domain.Passenger;
import domain.SeatId;

/**
 * One passenger's request as part of a purchase: either an explicitly chosen seat, or an
 * automatic assignment when {@code chosenSeatId} is absent.
 */
public record TicketRequest(Passenger passenger, SeatId chosenSeatId) {

    public static TicketRequest automaticSeat(Passenger passenger) {
        return new TicketRequest(passenger, null);
    }

    public static TicketRequest chosenSeat(Passenger passenger, SeatId seatId) {
        return new TicketRequest(passenger, seatId);
    }

    public boolean hasChosenSeat() {
        return chosenSeatId != null;
    }
}
