package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Reference behavioral tests for {@link TrajetSeat}.
 */
class TrajetSeatTest {

    private final TrajetId trajetId = TrajetId.newId();
    private final SeatId seatId = SeatId.newId();

    @Test
    void a_new_trajet_seat_is_available() {
        TrajetSeat seat = new TrajetSeat(trajetId, seatId);

        assertEquals(TrajetSeatStatus.AVAILABLE, seat.status());
    }

    @Test
    void reserving_an_available_seat_makes_it_reserved() {
        TrajetSeat seat = new TrajetSeat(trajetId, seatId);

        seat.reserve();

        assertEquals(TrajetSeatStatus.RESERVED, seat.status());
    }

    @Test
    void reserved_seat_cannot_be_reserved_again() {
        TrajetSeat seat = new TrajetSeat(trajetId, seatId);
        seat.reserve();

        assertThrows(SeatAlreadyReservedException.class, seat::reserve);
    }

    @Test
    void releasing_a_reserved_seat_makes_it_available_again() {
        TrajetSeat seat = new TrajetSeat(trajetId, seatId);
        seat.reserve();

        seat.release();

        assertEquals(TrajetSeatStatus.AVAILABLE, seat.status());
    }
}
