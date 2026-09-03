package domain;

/** Thrown when trying to reserve a seat that is already reserved for the same trajet. */
public class SeatAlreadyReservedException extends RuntimeException {

    public SeatAlreadyReservedException(TrajetId trajetId, SeatId seatId) {
        super("Seat " + seatId.value() + " is already reserved for trajet " + trajetId.value());
    }
}
