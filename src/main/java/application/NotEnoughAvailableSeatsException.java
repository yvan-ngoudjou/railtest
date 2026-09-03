package application;

import domain.TrajetId;

/** Thrown when a purchase requests more seats than are currently available on the trajet. */
public class NotEnoughAvailableSeatsException extends RuntimeException {

    public NotEnoughAvailableSeatsException(TrajetId trajetId, int requested, int available) {
        super("Trajet " + trajetId.value() + " has only " + available
                + " available seat(s), " + requested + " requested");
    }
}
