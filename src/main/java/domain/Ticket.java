package domain;

/** The right to travel: issued to one passenger, for one seat, on one trajet. */
public record Ticket(TicketId id, TrajetId trajetId, PassengerId passengerId, SeatId seatId) {

    /** Issues a new ticket, generating its id. Callers should not invent ids themselves. */
    public static Ticket issueFor(TrajetId trajetId, PassengerId passengerId, SeatId seatId) {
        return new Ticket(TicketId.newId(), trajetId, passengerId, seatId);
    }
}
