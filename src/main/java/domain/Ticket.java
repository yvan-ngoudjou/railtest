package domain;

/** The right to travel: issued to one passenger, for one seat, on one trajet. */
public record Ticket(TicketId id, TrajetId trajetId, PassengerId passengerId, SeatId seatId) {
}
